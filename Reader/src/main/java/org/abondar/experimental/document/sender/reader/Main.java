package org.abondar.experimental.document.sender.reader;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;


public class Main {
    public static void main(String[] args) throws Exception {

        var camelContext = new DefaultCamelContext();

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure()  {
                from("kafka:documentSender?brokers=localhost:9092")
                        .routeId("readFromKafka")
                        .process(exchange ->
                                System.out.println(exchange.getIn().getBody()));
            }
        });

        camelContext.start();
    }
}
