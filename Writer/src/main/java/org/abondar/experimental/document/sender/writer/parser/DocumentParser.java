package org.abondar.experimental.document.sender.writer.parser;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class for parsing of incoming document
 *
 * @author a.bondar
 */
@Component
public class DocumentParser {

    /**
     * Basic method for parsing
     *
     * @param file - incoming file to parse
     */
    public void parseDocument(InputStream file) throws IOException, SAXException, TikaException {

        var detector = new DefaultDetector();
        var metadata = new Metadata();
        var mediaType = detector.detect(file, metadata);
        var contentHandler = new BodyContentHandler();
        var parseContext = new ParseContext();

        var parser = new AutoDetectParser();
        parser.parse(file, contentHandler, metadata, parseContext);

        System.out.println(mediaType.toString());
        System.out.println(contentHandler.toString());
        System.out.println(metadata.toString());
    }


}
