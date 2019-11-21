package org.abondar.experimental.document.sender.writer.service;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import javax.ws.rs.core.Response;

public class FileUploadTestImpl implements FileUploadService {
    @Override
    public Response uploadFile(MultipartBody body) {

        return Response.ok(ResponseMessageUtil.FILE_UPLOADED).build();
    }
}
