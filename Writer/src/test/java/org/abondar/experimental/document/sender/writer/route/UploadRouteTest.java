package org.abondar.experimental.document.sender.writer.route;

import org.abondar.experimental.document.sender.writer.WriterApplication;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;


import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

@CamelSpringBootTest
@EnableAutoConfiguration
@SpringBootTest(classes = {WriterApplication.class, UploadRoute.class})
public class UploadRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:kafka:test")
    private MockEndpoint mockKafkaEndpoint;

    @Test
    public void sendToKafkaTest() throws Exception {

        AdviceWith.adviceWith(
                producerTemplate.getCamelContext(),
                "kafkaSend",
                a-> a.interceptSendToEndpoint("direct:sendToKafka")
                        .skipSendToOriginalEndpoint()
                        .to(mockKafkaEndpoint.getEndpointUri())
        );

        var req = new MockMultipartHttpServletRequest();

        var fStream = UploadRouteTest.class.getResourceAsStream("/test.doc");
        var file = new MockMultipartFile("file","test.doc",
                "application/msword",fStream);

        req.addFile(file);

        Exchange exchange = producerTemplate.getCamelContext().getEndpoint("direct:sendToKafka").createExchange();
        exchange.getIn().setBody(exchange);

        producerTemplate.send("direct:sendToKafka",exchange);
        mockKafkaEndpoint.assertIsSatisfied();
        mockKafkaEndpoint.expectedBodiesReceived();
        mockKafkaEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "createUser");
        mockKafkaEndpoint.expectedMessageCount(1);
        mockKafkaEndpoint.reset();
    }
}
