package com.homeProject.JennsArtWebsite.client;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DropboxClient {
    private final DbxClientV2 dropboxClient;

    @Autowired
    public DropboxClient(@Value("${dropbox.app-name}") String appName,
                         @Value("${dropbox.access-token}") String accessToken) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).build();
        this.dropboxClient = new DbxClientV2(config, accessToken);
    }

    public DbxClientV2 getDropboxClient() {
        return dropboxClient;
    }
}
