package com.sberbank.cms.configuration;

import com.sberbank.batch.Param;
import com.sberbank.batch.Targeting;
import com.sberbank.cms.backend.content.Campaign;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.StaxWriterCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

@Configuration
@EnableBatchProcessing
@Log
public class ExportJob {
    private static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";

    @Bean
    public ApplicationContextFactory targeting() {
        return new GenericApplicationContextFactory(TargetingExport.class);
    }

    @Bean
    public ApplicationContextFactory template() {
        return new GenericApplicationContextFactory(TemplateExport.class);
    }

    @Configuration
    public static class TemplateExport {
        private final JobBuilderFactory jobBuilderFactory;
        private final StepBuilderFactory stepBuilderFactory;
        private final DataSource dataSource;
        private final ConversionService conversionService;

        public TemplateExport(JobBuilderFactory j, StepBuilderFactory s, DataSource dataSource, ConversionService conversionService) {
            this.jobBuilderFactory = j;
            this.stepBuilderFactory = s;
            this.dataSource = dataSource;
            this.conversionService = conversionService;
        }

        @Bean
        public Job templateJob() {
            return jobBuilderFactory.get("template").start(templateStep()).build();
        }

        @Bean
        public Step templateStep() {
            return stepBuilderFactory.get("template step").<Campaign, Campaign>chunk(10).
                    reader(templateReader()).
                    processor(item -> {
                        log.info(item.toString());
                        return item;
                    }).
                    writer(templateWriter()).
                    build();
        }

        @Bean
        public JdbcPagingItemReader<Campaign> templateReader() {
            JdbcPagingItemReader<Campaign> reader = new JdbcPagingItemReader<>();
            reader.setDataSource(dataSource);
            reader.setPageSize(1);
            BeanPropertyRowMapper<Campaign> rowMapper = new BeanPropertyRowMapper<>(Campaign.class);
            rowMapper.setConversionService(conversionService);
            reader.setRowMapper(rowMapper);


            SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
            queryProvider.setSelectClause("*");
            queryProvider.setFromClause("campaign");
            queryProvider.setDataSource(dataSource);
            queryProvider.setSortKey("id");
            try {
                reader.setQueryProvider(queryProvider.getObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reader;
        }

        @Bean
        public StaxEventItemWriter<Campaign> templateWriter() {
            StaxEventItemWriter<Campaign> writer = new StaxEventItemWriter<>();
            writer.setResource(new FileSystemResource("xml/template.xml"));
            writer.setMarshaller(marshaller());
            writer.setRootTagName("data");
            writer.setHeaderCallback(header("templates"));
            writer.setFooterCallback(footer("templates"));
            return writer;
        }

        @Bean
        public XStreamMarshaller marshaller() {
            XStreamMarshaller m = new XStreamMarshaller();

            Map<String, Class> aliases = new HashMap();
            aliases.put("targeting", Targeting.class);
            aliases.put("param", Param.class);
            m.setAliases(aliases);
            return m;
        }
    }

    @Configuration
    public static class TargetingExport {
        private final JobBuilderFactory jobBuilderFactory;
        private final StepBuilderFactory stepBuilderFactory;

        public TargetingExport(JobBuilderFactory j, StepBuilderFactory s) {
            this.jobBuilderFactory = j;
            this.stepBuilderFactory = s;
        }

        @Bean
        public Job targetingJob() {
            return jobBuilderFactory.get("targeting").start(targetingStep()).build();
        }

        @Bean
        public Step targetingStep() {
            return stepBuilderFactory.get("targeting step").<Targeting, Targeting>chunk(10).
                    reader(reader()).
                    processor(item -> {
                        log.info(item.toString());
                        return item;
                    }).
                    writer(writer()).
                    build();
        }

        @Bean
        public FlatFileItemReader<Targeting> reader() {
            FlatFileItemReader<Targeting> rd = new FlatFileItemReader<>();
            rd.setResource(new ClassPathResource("csv/targeting.csv"));
            rd.setLineMapper(lineMapper());
            return rd;
        }

        @NotNull
        private DefaultLineMapper<Targeting> lineMapper() {
            DefaultLineMapper<Targeting> mp = new DefaultLineMapper<>();
            mp.setLineTokenizer(new DelimitedLineTokenizer(";"));
            mp.setFieldSetMapper(fieldMapper());
            return mp;
        }

        @NotNull
        private FieldSetMapper<Targeting> fieldMapper() {
            return fs -> {
                Targeting t = new Targeting();
                t.setClient(fs.readRawString(0));
                t.setCombined_template_id(fs.readRawString(1));
                t.setTip_id(fs.readRawString(2));
                t.setStart_datetime(fs.readDate(3, YYYY_MM_DD_T_HH_MM_SS));
                t.setExpiration_datetime(fs.readDate(4, YYYY_MM_DD_T_HH_MM_SS));
                t.setWeight(fs.readDouble(5));

                List<Param> params = new ArrayList<>();
                for (int i = 6; i < fs.getFieldCount(); i++) {
                    params.add(new Param("param" + i, fs.readRawString(i)));
                }
                t.setParams(params);
                return t;
            };
        }

        @Bean
        public StaxEventItemWriter<Targeting> writer() {
            StaxEventItemWriter<Targeting> writer = new StaxEventItemWriter<>();
            writer.setResource(new FileSystemResource("xml/targeting.xml"));
            writer.setMarshaller(marshaller());
            writer.setRootTagName("data");
            writer.setHeaderCallback(header("targetings"));
            writer.setFooterCallback(footer("targetings"));
            return writer;
        }

        @Bean
        public XStreamMarshaller marshaller() {
            XStreamMarshaller m = new XStreamMarshaller();

            Map<String, Class> aliases = new HashMap();
            aliases.put("targeting", Targeting.class);
            aliases.put("param", Param.class);
            m.setAliases(aliases);
            return m;
        }
    }

    @NotNull
    private static StaxWriterCallback footer(String tag) {
        return wr -> {
            try {
                XMLEventFactory f = XMLEventFactory.newInstance();
                wr.add(f.createEndElement("", "", tag));
            } catch (XMLStreamException e) {
                log.log(Level.SEVERE, "Error while writing file footer", e);
            }
        };
    }

    @NotNull
    private static StaxWriterCallback header(String tag) {
        return wr -> {
            try {
                XMLEventFactory f = XMLEventFactory.newInstance();
                wr.add(f.createStartElement("", "", "datetime"));
                wr.add(f.createCharacters(new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS).format(new Date())));
                wr.add(f.createEndElement("", "", "datetime"));
                wr.add(f.createStartElement("", "", tag));
            } catch (XMLStreamException e) {
                log.log(Level.SEVERE, "Error while writing file header", e);
            }
        };
    }

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService service = new DefaultConversionService();
        service.addConverter(new Json2MapConverter());
        return service;
    }

    /*@Bean
    public RunScheduler scheduler() {
        return new RunScheduler(null, null);
    }*/
}