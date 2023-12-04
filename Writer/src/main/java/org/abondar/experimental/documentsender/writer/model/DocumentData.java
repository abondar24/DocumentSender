package org.abondar.experimental.documentsender.writer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * POJO with file data and contents
 *
 * @author a.bondar
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DocumentData {

    private String mediaType;
    private String content;
    private List<String> metadata;
}
