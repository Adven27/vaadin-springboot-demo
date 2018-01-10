package com.sberbank.cms.configuration;

import com.sberbank.batch.Param;
import com.sberbank.cms.backend.content.Campaign;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Log
@Configuration
public class TemplateExport {
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
        JdbcPagingItemReader<Campaign> rd = new JdbcPagingItemReader<>();
        rd.setDataSource(dataSource);
        rd.setPageSize(1);
        rd.setRowMapper(rowMapper());
        try {
            rd.setQueryProvider(queryProvider().getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rd;
    }

    @NotNull
    private BeanPropertyRowMapper<Campaign> rowMapper() {
        BeanPropertyRowMapper<Campaign> rm = new BeanPropertyRowMapper<>(Campaign.class);
        rm.setConversionService(conversionService);
        return rm;
    }

    @NotNull
    private SqlPagingQueryProviderFactoryBean queryProvider() {
        SqlPagingQueryProviderFactoryBean qp = new SqlPagingQueryProviderFactoryBean();
        qp.setDataSource(dataSource);
        qp.setSelectClause("*");
        qp.setFromClause("campaign");
        qp.setSortKey("id");
        return qp;
    }

    @Bean
    public StaxEventItemWriter<Campaign> templateWriter() {
        StaxEventItemWriter<Campaign> writer = new StaxEventItemWriter<>();
        writer.setResource(new FileSystemResource("xml/template.xml"));
        writer.setMarshaller(marshaller());
        writer.setRootTagName("data");
        writer.setHeaderCallback(ExportJob.header("templates"));
        writer.setFooterCallback(ExportJob.footer("templates"));
        return writer;
    }

    @Bean
    public XStreamMarshaller marshaller() {
        XStreamMarshaller m = new XStreamMarshaller();

        Map<String, Class> aliases = new HashMap();
        aliases.put("template", Campaign.class);
        aliases.put("param", Param.class);
        m.setAliases(aliases);
        return m;
    }
}