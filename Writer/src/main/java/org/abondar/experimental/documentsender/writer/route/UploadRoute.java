package org.abondar.experimental.documentsender.writer.route;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.abondar.experimental.documentsender.data.avro.Document;
import org.abondar.experimental.documentsender.writer.model.UploadResponse;
import org.abondar.experimental.documentsender.writer.parser.DocumentParser;
import org.abondar.experimental.documentsender.writer.properties.KafkaProperties;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import static org.apache.camel.model.rest.RestParamType.body;
import static org.apache.camel.model.rest.RestParamType.formData;

@Component
public class UploadRoute extends RouteBuilder {

    private final DocumentParser documentParser;
    private final KafkaProperties kafkaProperties;

    private final ObjectMapper objectMapper;

    @Autowired
    public UploadRoute(DocumentParser documentParser, KafkaProperties kafkaProperties, ObjectMapper mapper) {
        this.documentParser = documentParser;
        this.kafkaProperties = kafkaProperties;
        this.objectMapper = mapper;
    }

    @Override
    public void configure() throws Exception {


        rest("/v1")
                .bindingMode(RestBindingMode.off)

                .post("/upload")
                .routeId("upload")
                .apiDocs(true)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .consumes(MediaType.MULTIPART_FORM_DATA_VALUE)
                .description("File to be uploaded for parsing")
                .type(File.class)
                .outType(UploadResponse.class)

                .param()
                .name("body")
                .type(formData)
                .description("File content")
                .endParam()

                .responseMessage()
                .code(200)
                .message("File uploaded successfully")
                .endResponseMessage()
                .to("direct:sendToKafka");


        from("direct:sendToKafka").routeId("kafkaSend")
                .log(LoggingLevel.DEBUG, "#{headers}")
                .process(exchange -> {
                    var req = exchange.getIn().getBody(InputStream.class);
                    var res = documentParser.parseDocument(req);
                    var doc = Document.newBuilder()
                            .setContent(res.content())
                            .setMediaType(res.mediaType())
                            .setMetadata(res.metadata());


                    exchange.getMessage().setBody(doc.build());
                })
                .to("kafka:" + kafkaProperties.getTopicName() + "?brokers=" + kafkaProperties.getBroker())
                .process(exchange -> {

                    String jsonResponse = objectMapper.writeValueAsString(new UploadResponse("File uploaded successfully"));

                    // Set HTTP response code and body before completing the exchange
                    exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
                    exchange.getMessage().setHeader(Exchange.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE);
                    exchange.getMessage().setBody(jsonResponse);
                });
    }

}

