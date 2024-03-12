package org.abondar.experimental.documentsender.writer.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "writer")
@Data
public class WriterProperties {

    private String username;

    private String password;
}
