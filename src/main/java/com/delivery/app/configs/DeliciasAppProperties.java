package com.delivery.app.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "delicias")
@Getter
@Setter
public class DeliciasAppProperties {

    private Boolean production;
    private Integer timezone;
    private CloudFiles files;

    @Getter
    @Setter
    public static class CloudFiles {
        private String resources;
        private String staticDefault;
        private String server;
        private UploadFile upload;

        @Getter
        @Setter
        public static class UploadFile {
            private String path;
        }
    }

}
