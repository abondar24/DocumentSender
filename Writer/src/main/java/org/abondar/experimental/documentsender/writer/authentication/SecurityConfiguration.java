package org.abondar.experimental.documentsender.writer.authentication;

import lombok.RequiredArgsConstructor;
import org.abondar.experimental.documentsender.writer.properties.WriterProperties;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;


@RequiredArgsConstructor
@Import(WriterProperties.class)
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final WriterProperties writerProperties;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(hs -> hs.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth-> auth.requestMatchers("/api/v1/**")
                        .hasRole("USER")
                        .anyRequest()
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .build();

    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(){
        UserDetails userDetails = User.builder()
                .username(writerProperties.getUsername())
                .password(passwordEncoder().encode(writerProperties.getPassword()))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

}
