package org.abondar.experimental.documentsender.writer;


import org.abondar.experimental.documentsender.data.avro.Document;
import org.abondar.experimental.documentsender.writer.model.DocumentData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentBuilderTest {

    @Test
    public void buildDocumentObjectTest(){
        var data = new DocumentData("test","test", List.of("test"));

        var doc =   Document.newBuilder()
                .setContent(data.content())
                .setMediaType(data.mediaType())
                .setMetadata(data.metadata()).build();

        assertEquals("test",doc.getContent());
        assertEquals("test",doc.getMediaType());

    }
}
