package org.abondar.experimental.documentsender.writer.route;


import org.abondar.experimental.documentsender.data.avro.Document;
import org.abondar.experimental.documentsender.writer.parser.DocumentParser;
import org.abondar.experimental.documentsender.writer.properties.KafkaProperties;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class UploadRoute extends RouteBuilder {

    private final DocumentParser documentParser;
    private final KafkaProperties kafkaProperties;

    @Autowired
    public UploadRoute(DocumentParser documentParser, KafkaProperties kafkaProperties) {
        this.documentParser = documentParser;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .apiHost("localhost")
                .port(8020)
                .apiContextPath("/sender");

        rest()
                .post()
                .path("/upload")
                .routeId("upload")
                .consumes("multipart/form-data")
                .produces("application/json")
                .apiDocs(true)
                .description("Path to upload file")
               .to("direct:sendToKafka");

        from("direct:sendToKafka").routeId("kafkaSend")
                .log(LoggingLevel.DEBUG, "#{headers}")
                .process(exchange -> {
                    var req = exchange.getIn().getBody(InputStream.class);
                    var res = documentParser.parseDocument(req);
                    var doc = Document.newBuilder()
                            .setContent(res.getContent())
                            .setMediaType(res.getMediaType())
                            .setMetadata(res.getMetadata());

                    exchange.getMessage().setBody(doc.build());
                })
                .to("kafka:" + kafkaProperties.getTopicName() + "?brokers=" + kafkaProperties.getBroker());
    }

}

