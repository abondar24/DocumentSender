package org.abondar.experimental.document.sender.writer.route;

import org.abondar.experimental.document.sender.writer.avro.Document;
import org.abondar.experimental.document.sender.writer.model.DocumentData;
import org.abondar.experimental.document.sender.writer.parser.DocumentParser;
import org.abondar.experimental.document.sender.writer.properties.KafkaProperties;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Component
public class UploadRoute extends RouteBuilder {

    private final DocumentParser documentParser;

    private DocumentData res;

    private final KafkaProperties kafkaProperties;

    @Autowired
    public UploadRoute(DocumentParser documentParser, KafkaProperties kafkaProperties) {
        this.documentParser = documentParser;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public void configure() throws Exception {

        rest()
                .post()
                .path("/upload")
                .routeId("upload")
                .consumes("multipart/form-data")
                .produces("application/json")
                .apiDocs(true)
                .to("direct:sendToKafka");

        from("direct:sendToKafka").routeId("kafkaSend")
                .log(LoggingLevel.DEBUG, "#{headers}")
                .process(exchange -> {
                    var req = exchange.getIn().getBody(MultipartHttpServletRequest.class);
                    res = documentParser.parseDocument(req.getFile("file").getInputStream());
                    var doc = Document.newBuilder()
                            .setContent(res.getContent())
                            .setMediaType(res.getMediaType())
                            .setMetadata(res.getMetadata());

                    exchange.getMessage().setBody(doc.build());
                })
                .to("kafka:"+kafkaProperties.getTopicName()+"?brokers="+kafkaProperties.getBroker());
    }

}

