package org.abondar.experimental.documentsender.reader;


import org.abondar.experimental.documentsender.data.avro.Document;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.IOException;


public class Reader {
    public static void main(String[] args) throws Exception {

        var camelContext = new DefaultCamelContext();

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("kafka:documentSender?brokers=localhost:9092")
                        .routeId("readFromKafka")
                        .process(exchange -> System.out.println(deserealizeMessage(exchange.getMessage()).getContent()));
            }
        });

        camelContext.start();
        while (true) {

        }
    }

    private static Document deserealizeMessage(Message message) throws IOException {
        var reader = new SpecificDatumReader<>(Document.class);
        var decoder = DecoderFactory.get().jsonDecoder(
                Document.getClassSchema(), (String) message.getBody());
        return reader.read(null, decoder);

    }
}
