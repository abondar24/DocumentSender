package org.abondar.experimental.documentsender.writer.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentParserTest {

    private DocumentParser parser;
    private String testContent = "Big Test";

    @BeforeEach
    public void setUp() {
        parser = new DocumentParser();
    }

    @Test
    public void testParserDoc() throws Exception {
        var data = parser.parseDocument(DocumentParserTest.class.getResourceAsStream("/test.doc"));

        assertTrue(data.content().contains(testContent));
        assertEquals("application/x-tika-msoffice",data.mediaType());
        assertEquals("application/msword",data.metadata().get(0));
    }

    @Test
    public void testParserPdf() throws Exception {
        var data = parser.parseDocument(DocumentParserTest.class.getResourceAsStream("/test.pdf"));

        assertTrue(data.content().contains(testContent));
        assertEquals("application/pdf",
                data.mediaType());
        assertEquals("application/pdf",data.metadata().get(0));


    }

    @Test
    public void testParserDocx() throws Exception {
        var data = parser.parseDocument(DocumentParserTest.class.getResourceAsStream("/test.docx"));

        assertTrue(data.content().contains(testContent));
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                data.mediaType());
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", data.metadata().get(0));
    }

    @Test
    public void testParserOdt() throws Exception {
        var data = parser.parseDocument(DocumentParserTest.class.getResourceAsStream("/test.odt"));

        assertTrue(data.content().contains(testContent));
        assertEquals("application/vnd.oasis.opendocument.text",
                data.mediaType());
        assertEquals("application/vnd.oasis.opendocument.text", data.metadata().get(0));
    }
}
