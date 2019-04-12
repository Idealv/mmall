package com.joe.product.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "product")
@Getter@Setter
public class ProductProperties {
    private String imageHost = "ftp.server.http.prefix";
}
