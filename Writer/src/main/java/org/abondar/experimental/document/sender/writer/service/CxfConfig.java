package org.abondar.experimental.document.sender.writer.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rest server configuration class
 *
 * @author a.bondar
 */
@Configuration
@Component
public class CxfConfig implements WebMvcConfigurer {

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }



    @Bean
    public JAXRSServerFactoryBean jaxRsServer(JacksonJsonProvider jsonProvider) {

        var factory = new JAXRSServerFactoryBean();
        factory.setBus(springBus());
        factory.setServiceClass(FileUploadService.class);
        factory.setProviders(List.of(jsonProvider));

        Map<Object, Object> extMappings = new HashMap<>();
        extMappings.put("json", "application/json");
        factory.setExtensionMappings(extMappings);
        Map<Object, Object> langMappings = new HashMap<>();
        langMappings.put("en", "en-us");

        factory.setInInterceptors(List.of(new LoggingInInterceptor()));
        factory.setOutInterceptors(List.of(new LoggingOutInterceptor()));

        factory.setLanguageMappings(langMappings);
        factory.setAddress("/sender");

        return factory;
    }


    @Bean
    public JacksonJsonProvider jsonProvider() {
        var provider = new JacksonJsonProvider();
        provider.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        provider.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        return provider;
    }

}
