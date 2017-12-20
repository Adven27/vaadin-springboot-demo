package com.sberbank.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

@Configuration
@EnableBatchProcessing
public class ExportJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ExportJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job scheduledJob() {
        return jobBuilderFactory.get("scheduledJob").flow(step1()).end().build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Targeting, Targeting>chunk(10)
                .reader(reader()).writer(writer()).build();
    }

    @Bean
    public FlatFileItemReader<Targeting> reader() {
        FlatFileItemReader<Targeting> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("csv/targeting.csv"));

        DefaultLineMapper<Targeting> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(";");
        tokenizer.setIncludedFields(new int[]{1, 2, 3, 4, 5, 6});
        tokenizer.setNames(new String[]{"сlient_id", "template_id", "model_id", "start_datetime", "end_datetime", "weight"});
        lineMapper.setLineTokenizer(tokenizer);

        BeanWrapperFieldSetMapper<Targeting> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Targeting.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public StaxEventItemWriter<Targeting> writer() {
        StaxEventItemWriter<Targeting> writer = new StaxEventItemWriter<>();
        writer.setResource(new FileSystemResource("xml/student.xml"));
        writer.setMarshaller(marshaller());
        writer.setRootTagName("students");
        return writer;
    }

    @Bean
    public XStreamMarshaller marshaller() {
        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setSupportedClasses(Targeting.class);
        return marshaller;
    }

    @Bean
    public RunScheduler scheduler() {
        return new RunScheduler(null, null);
    }
}