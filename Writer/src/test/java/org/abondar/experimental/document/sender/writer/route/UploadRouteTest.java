package org.abondar.experimental.document.sender.writer.route;

import org.abondar.experimental.document.sender.writer.WriterApplication;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.activation.DataHandler;
import javax.ws.rs.core.MultivaluedHashMap;
import java.util.Map;

@SpringBootTest(classes = WriterApplication.class)
@RunWith(CamelSpringBootRunner.class)
@MockEndpoints
public class UploadRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;


    @EndpointInject(uri = "mock:sendToKafka")
    private MockEndpoint mockEndpoint;


    @Test
    public void uploadFileRouteTest() throws Exception {

        producerTemplate.sendBodyAndHeaders("direct:uploadFile",
                new MultipartBody(new Attachment("test",
                        new DataHandler(UploadRouteTest.class.getResourceAsStream("/test.doc"), "test"),
                        new MultivaluedHashMap<>())),
                Map.of());

        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "createUser");
        mockEndpoint.expectedMessageCount(1);


        mockEndpoint.reset();
    }
}
