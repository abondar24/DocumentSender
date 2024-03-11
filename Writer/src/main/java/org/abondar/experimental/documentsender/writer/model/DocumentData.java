package org.abondar.experimental.documentsender.writer.model;


import java.util.List;

/**
 * POJO with file data and contents
 *
 * @author a.bondar
 */

public record DocumentData(
        String mediaType,
        String content,
        List<String> metadata
) {


}
