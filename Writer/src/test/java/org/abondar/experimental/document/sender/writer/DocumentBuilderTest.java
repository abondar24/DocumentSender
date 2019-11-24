package org.abondar.experimental.document.sender.writer;

import org.abondar.experimental.document.sender.writer.avro.Document;
import org.abondar.experimental.document.sender.writer.model.DocumentData;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentBuilderTest {

    @Test
    public void buildDocumentObjectTest(){
        var data = new DocumentData("test","test", List.of("test"));

        var doc =   Document.newBuilder()
                .setContent(data.getContent())
                .setMediaType(data.getMediaType())
                .setMetadata(data.getMetadata()).build();

        assertEquals("test",doc.getContent());
        assertEquals("test",doc.getMediaType());

    }
}
