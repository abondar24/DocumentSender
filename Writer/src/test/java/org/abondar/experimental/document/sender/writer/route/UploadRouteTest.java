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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@CamelSpringBootTest
@EnableAutoConfiguration
@SpringBootTest(classes = {WriterApplication.class, UploadRoute.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UploadRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:kafka:test")
    private MockEndpoint mockKafkaEndpoint;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void sendToKafkaTest() throws Exception {

        AdviceWith.adviceWith(
                producerTemplate.getCamelContext(),
                "kafkaSend",
                a -> a.interceptSendToEndpoint("direct:sendToKafka")
                        .skipSendToOriginalEndpoint()
                        .to(mockKafkaEndpoint.getEndpointUri())
        );

        var fStream = UploadRouteTest.class.getResourceAsStream("/test.doc");

        Exchange exchange = producerTemplate.getCamelContext().getEndpoint("direct:sendToKafka").createExchange();
        exchange.getIn().setBody(fStream);

        producerTemplate.send("direct:sendToKafka", exchange);
        mockKafkaEndpoint.assertIsSatisfied();
        mockKafkaEndpoint.expectedBodiesReceived();
        mockKafkaEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "createUser");
        mockKafkaEndpoint.expectedMessageCount(1);
        mockKafkaEndpoint.reset();
    }


    @Test
    public void uploadRestTest() throws Exception {
        AdviceWith.adviceWith(
                producerTemplate.getCamelContext(),
                "kafkaSend",
                a -> a.interceptSendToEndpoint("kafka:*")
                        .skipSendToOriginalEndpoint()
                        .to(mockKafkaEndpoint.getEndpointUri())
        );

        var fStream = UploadRouteTest.class.getResourceAsStream("/test.doc");
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", fStream.readAllBytes());


        webTestClient
                .post()
                .uri("/sender/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=test.doc")
                .bodyValue(parts)
                .exchange()
                .expectStatus().isOk();


        mockKafkaEndpoint.assertIsSatisfied();
        mockKafkaEndpoint.expectedBodiesReceived();
        mockKafkaEndpoint.reset();
    }
}
