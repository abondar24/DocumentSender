package org.abondar.experimental.documentsender.writer.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "writer")
@Getter
@Setter
public class WriterProperties {

    private String username;
    private String password;
}
