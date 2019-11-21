package org.abondar.experimental.document.sender.writer.service;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.document.sender.writer.WriterApplication;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = WriterApplication.class)
@ExtendWith(SpringExtension.class)
public class FileUploadServiceTest {

    private static final String ENDPOINT = "local://sender_test";

    @BeforeAll
    public static void beforeMethod() {
        var factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(ENDPOINT);
        factory.setServiceBean(new FileUploadTestImpl());
        Server server = factory.create();
        server.start();
    }

    @Test
    public void testFileUpload(){
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/upload").accept(MediaType.APPLICATION_JSON);

        client.type(MediaType.MULTIPART_FORM_DATA);
        ContentDisposition cd = new ContentDisposition("attachment;filename=test.doc");
        Attachment att = new Attachment("root", FileUploadServiceTest.class.getResourceAsStream("/test.doc"), cd);

        var resp = client.post(new MultipartBody(att));
        assertEquals(200, resp.getStatus());

        var res = resp.readEntity(String.class);
        assertEquals(ResponseMessageUtil.FILE_UPLOADED, res);
    }

}
