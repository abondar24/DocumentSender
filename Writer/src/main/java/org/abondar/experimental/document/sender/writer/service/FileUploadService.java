package org.abondar.experimental.document.sender.writer.service;


import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;

public interface FileUploadService extends Serializable {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    Response uploadFile(MultipartBody body);
}
