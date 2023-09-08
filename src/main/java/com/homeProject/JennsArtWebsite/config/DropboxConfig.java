package com.homeProject.JennsArtWebsite.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DropboxConfig {

    @Value("${dropbox.api.access-token}")
    private String dropboxAccessToken;
    @Value("${dropbox.api.endpoints.upload}")
    private String uploadEndpoint;
    @Value("${dropbox.api.endpoints.download}")
    private String downloadEndpoint;
    @Value("${dropbox.api.endpoints.delete}")
    private String deleteEndpoint;
    @Value("${dropbox.api.endpoints.thumbnail}")
    private String thumbnailEndpoint;
    @Value("${dropbox.api.endpoints.thumbnail_batch}")
    private String thumbnailBatchEndpoint;
}
