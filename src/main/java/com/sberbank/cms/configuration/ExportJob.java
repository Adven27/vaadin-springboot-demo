package com.sberbank.cms.configuration;

import lombok.extern.java.Log;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.batch.item.xml.StaxWriterCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

@Log
@Configuration
@EnableBatchProcessing
public class ExportJob {
    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";

    @Bean
    public ApplicationContextFactory targeting() {
        return new GenericApplicationContextFactory(TargetingExport.class);
    }

    @Bean
    public ApplicationContextFactory template() {
        return new GenericApplicationContextFactory(TemplateExport.class);
    }

    public static StaxWriterCallback header(String tag) {
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

    public static StaxWriterCallback footer(String tag) {
        return wr -> {
            try {
                XMLEventFactory f = XMLEventFactory.newInstance();
                wr.add(f.createEndElement("", "", tag));
            } catch (XMLStreamException e) {
                log.log(Level.SEVERE, "Error while writing file footer", e);
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