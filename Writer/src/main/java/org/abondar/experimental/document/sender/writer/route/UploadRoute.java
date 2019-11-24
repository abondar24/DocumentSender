package org.abondar.experimental.document.sender.writer.route;

import org.abondar.experimental.document.sender.writer.avro.Document;
import org.abondar.experimental.document.sender.writer.model.DocumentData;
import org.abondar.experimental.document.sender.writer.parser.DocumentParser;
import org.abondar.experimental.document.sender.writer.service.ResponseMessageUtil;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

@Component
public class UploadRoute extends RouteBuilder {

    private DocumentParser documentParser;


    @Autowired
    public UploadRoute(DocumentParser documentParser) {
        this.documentParser = documentParser;
    }

    @Override
    public void configure() throws Exception {

        from("cxfrs:bean:jaxRsServer?bindingStyle=SimpleConsumer&synchronous=true")
                .routeId("restServiceRoute")
                .toD("direct:${headers.operationName}", false);


        from("direct:uploadFile").routeId("upload")
                .log(LoggingLevel.DEBUG, "#{headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MultipartBody mBody = (MultipartBody) bdy;
                    try {
                        var dataHandler =mBody.getAllAttachments().get(0).getDataHandler();
                        var res = documentParser.parseDocument(dataHandler.getInputStream());

                        System.out.println(res);

                        return Response.ok(ResponseMessageUtil.FILE_UPLOADED).build();
                    } catch (IOException | SAXException | TikaException ex) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(ResponseMessageUtil.FILE_INCORRECT).build();
                    }
                })
                .wireTap("direct:sendToKafka");


        from("direct:sendToKafka").routeId("kafkaSend")
                .log(LoggingLevel.DEBUG, "#{headers}")
                .transform()
                .body((bdy, hdrs) -> {

                    //build full method
                 //   var doc = Document.newBuilder().setContent(res.getContent());
                  //  return doc.build();
                    return bdy;
                });
        //to("kafka");
    }

}

