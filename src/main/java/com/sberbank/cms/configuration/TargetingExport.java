package com.sberbank.cms.configuration;

import com.sberbank.batch.Param;
import com.sberbank.batch.Targeting;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
@Configuration
public class TargetingExport {
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
            t.setStart_datetime(fs.readDate(3, ExportJob.YYYY_MM_DD_T_HH_MM_SS));
            t.setExpiration_datetime(fs.readDate(4, ExportJob.YYYY_MM_DD_T_HH_MM_SS));
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
        writer.setHeaderCallback(ExportJob.header("targetings"));
        writer.setFooterCallback(ExportJob.footer("targetings"));
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
