package org.abondar.experimental.document.sender.writer.parser;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class DocumentParserTest {


    @Test
    public void testParserDoc() throws Exception {
        var parser = new DocumentParser();
        parser.parseDocument(new BufferedInputStream(new FileInputStream("/home/abondar/Documents/Resignation.doc")));

    }
}
