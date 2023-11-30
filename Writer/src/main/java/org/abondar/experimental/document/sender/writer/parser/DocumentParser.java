package org.abondar.experimental.document.sender.writer.parser;

import org.abondar.experimental.document.sender.writer.model.DocumentData;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public DocumentData parseDocument(InputStream file) throws IOException, SAXException, TikaException {

        var detector = new DefaultDetector();
        var metadata = new Metadata();
        var mediaType = detector.detect(file, metadata);
        var contentHandler = new BodyContentHandler();
        var parseContext = new ParseContext();

        var parser = new AutoDetectParser();
        parser.parse(file, contentHandler, metadata, parseContext);


        return new DocumentData(mediaType.toString(), contentHandler.toString(),getMetadata(metadata));
    }

    private List<String> getMetadata(Metadata metadata){
        List<String> metadataList = new ArrayList<>();
        metadataList.add(metadata.get("Content-Type"));
        metadataList.add(metadata.get("meta:creation-date"));
        metadataList.add(metadata.get("meta:save-date"));
        metadataList.add(metadata.get("modified"));
        metadataList.add(metadata.get("pdf:PDFVersion"));
        metadataList.add(metadata.get("xmp:CreatorTool"));

        return metadataList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


}
