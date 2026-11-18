package com.ling.lingaicodegeneration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.util.Set;

@Configuration(proxyBeanMethods = false)
@Profile("pilot")
public class PilotOriginConfig {
    public PilotOriginConfig(@Value("${app.preview.frame-origin}") URI frontend,
                             @Value("${app.preview.base-url}") URI preview,
                             @Value("${app.deploy.base-url}") URI publish) {
        validate(frontend);
        validate(preview);
        validate(publish);
        if (!frontend.getPath().isEmpty() || frontend.getHost().equalsIgnoreCase(preview.getHost()) ||
                frontend.getHost().equalsIgnoreCase(publish.getHost())) {
            throw new IllegalArgumentException("Set the frontend origin without a path and use separate hosts "
                    + "for generated content and authenticated application sessions");
        }
    }

    private static void validate(URI url) {
        String host = url.getHost();
        if (host == null || url.getUserInfo() != null || url.getQuery() != null || url.getFragment() != null) {
            throw new IllegalArgumentException("Invalid pilot origin or base URL");
        }
        boolean localHttp = "http".equals(url.getScheme()) && Set.of("localhost", "127.0.0.1", "[::1]").contains(host);
        if (!"https".equals(url.getScheme()) && !localHttp) {
            throw new IllegalArgumentException("Pilot URLs require HTTPS outside loopback");
        }
    }
}
