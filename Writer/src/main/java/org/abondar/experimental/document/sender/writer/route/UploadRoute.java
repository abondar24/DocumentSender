package org.abondar.experimental.document.sender.writer.route;

import org.abondar.experimental.document.sender.writer.avro.Document;
import org.abondar.experimental.document.sender.writer.model.DocumentData;
import org.abondar.experimental.document.sender.writer.parser.DocumentParser;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UploadRoute extends RouteBuilder {

    //TODO ADD CAMEL REST

//    @POST
//    @Path("/upload")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.APPLICATION_JSON)
//    Response uploadFile(MultipartBody body);
    private DocumentParser documentParser;

    private DocumentData res;

    @Autowired
    public UploadRoute(DocumentParser documentParser) {
        this.documentParser = documentParser;
    }

    @Override
    public void configure() throws Exception {

//        from("cxfrs:bean:jaxRsServer?bindingStyle=SimpleConsumer&synchronous=true")
//                .routeId("restServiceRoute")
//                .toD("direct:${headers.operationName}", false);
//
//
//        from("direct:uploadFile").routeId("upload")
//                .log(LoggingLevel.DEBUG, "#{headers}")
//                .transform()
//                .body((bdy, hdrs) -> {
//                    MultipartBody mBody = (MultipartBody) bdy;
//                    try {
//                        var dataHandler = mBody.getAllAttachments().get(0).getDataHandler();
//                        res = documentParser.parseDocument(dataHandler.getInputStream());
//
//                        return Response.ok(ResponseMessageUtil.FILE_UPLOADED).build();
//                    } catch (IOException | SAXException | TikaException ex) {
//                        return Response.status(Response.Status.BAD_REQUEST)
//                                .entity(ResponseMessageUtil.FILE_INCORRECT).build();
//                    }
//                })
//                .wireTap("direct:sendToKafka");


        from("direct:sendToKafka").routeId("kafkaSend")
                .log(LoggingLevel.DEBUG, "#{headers}")
                .transform()
                .body((bdy, hdrs) -> {

                   var doc = Document.newBuilder()
                            .setContent(res.getContent())
                            .setMediaType(res.getMediaType())
                            .setMetadata(res.getMetadata());
                    return doc.build();
                })
                .to("kafka:documentSender?brokers=localhost:9092");
    }

}

