package com.homeProject.JennsArtWebsite.service;


import com.homeProject.JennsArtWebsite.config.DropboxConfig;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.homeProject.JennsArtWebsite.exception.NoFileException;

import java.io.IOException;

@Service
@AllArgsConstructor
public class DropboxService {

    private final DropboxConfig dropboxConfig;

    public boolean uploadFileToDropbox(final MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new NoFileException("No file to upload");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Authorization", "Bearer " + dropboxConfig.getDropboxAccessToken());
        String dropboxApiArg = "{\"autorename\":false,\"mode\":\"add\",\"mute\":false,\"path\":\"/" + file.getOriginalFilename() + "\",\"strict_conflict\":false}";
        headers.set("Dropbox-API-Arg", dropboxApiArg);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);


        ResponseEntity<String> responseEntity = new RestTemplate().exchange(
                dropboxConfig.getUploadEndpoint(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return responseEntity.getStatusCode() == HttpStatus.OK;
    }

    public byte[] downloadFileFromDropbox(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Authorization", "Bearer " + dropboxConfig.getDropboxAccessToken());

        String dropboxApiArg = "{\"path\":\"" + path + "\"}";
        headers.set("Dropbox-API-Arg", dropboxApiArg);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> responseEntity = new RestTemplate().exchange(
                dropboxConfig.getDownloadEndpoint(),
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            System.err.println("Failed to download file from Dropbox. Status code: " + responseEntity.getStatusCode());
        }
        return null;
    }

    public byte[] getThumbnailFromDropbox(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Authorization", "Bearer " + dropboxConfig.getDropboxAccessToken());

        String dropboxApiArg = "{\"format\":\"jpeg\",\"mode\":\"strict\",\"quality\":\"quality_80\",\"resource\":{\".tag\":\"path\",\"path\":\"" + path + "\"},\"size\":\"w64h64\"}";
        ;
        headers.set("Dropbox-API-Arg", dropboxApiArg);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> responseEntity = new RestTemplate().exchange(
                dropboxConfig.getThumbnailEndpoint(),
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );

        return responseEntity.getStatusCode() == HttpStatus.OK
                ? responseEntity.getBody()
                : null;
    }

    public boolean deleteFileFromDropbox(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + dropboxConfig.getDropboxAccessToken());
        String requestJson = "{\"path\":\"" + path + "\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> responseEntity = new RestTemplate().exchange(
                dropboxConfig.getDeleteEndpoint(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        return responseEntity.getStatusCode() == HttpStatus.OK;
    }
}
