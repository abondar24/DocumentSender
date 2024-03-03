package org.abondar.experimental.documentsender.writer.authentication;

import com.sun.security.auth.UserPrincipal;
import org.abondar.experimental.documentsender.writer.properties.WriterProperties;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.Subject;
import java.security.Principal;

@Component
public class BasicAuthService implements Processor {

    private final WriterProperties writerProperties;

    @Autowired
    public BasicAuthService(WriterProperties writerProperties) {
        this.writerProperties = writerProperties;
    }


    @Override
    public void process(Exchange exchange) throws Exception {
        String userpass = new String(Base64.decodeBase64(exchange.getIn().getHeader("Authorization", String.class)));
        String[] tokens = userpass.split(":");

        if (tokens[0].equals(writerProperties.getUsername()) && tokens[1].equals(writerProperties.getPassword())){
            var principal = new UserPrincipal(tokens[0]);

            var subject = new Subject();
            subject.getPrincipals().add(principal);

            // place the Subject in the In message
            exchange.getIn().setHeader(Exchange.AUTHENTICATION, subject);
        }

    }
}
