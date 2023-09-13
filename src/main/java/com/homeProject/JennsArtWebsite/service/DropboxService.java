package com.homeProject.JennsArtWebsite.service;


import com.homeProject.JennsArtWebsite.config.DropboxConfig;
import com.homeProject.JennsArtWebsite.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class DropboxService {

    private final DropboxConfig dropboxConfig;
    private final WebClientConfig webClientConfig;

    public Mono<Void> uploadFileToDropbox(final MultipartFile file) throws IOException {
        String dropboxApiArgs = "{\"autorename\":false,\"mode\":\"add\",\"mute\":false,\"path\":\"/" + file.getOriginalFilename() + "\",\"strict_conflict\":false}";
        WebClient webClient = setupWebclient();

        return webClient.post()
                .uri(dropboxConfig.getUploadEndpoint())
                .headers(httpHeaders -> httpHeaders.addAll(createHeaders(dropboxApiArgs)))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(BodyInserters.fromResource(new ByteArrayResource(file.getBytes())))
                .retrieve()
                .bodyToMono(Void.class);
    }


    public Mono<byte[]> downloadFileFromDropbox(final String path) {
        String dropBoxApiArgs = "{\"path\":\"" + path + "\"}";
        WebClient webClient = setupWebclient();
        return webClient.post()
                .uri(dropboxConfig.getDownloadEndpoint())
                .headers(httpHeaders -> httpHeaders.addAll(createHeaders(dropBoxApiArgs)))
                .retrieve()
                .bodyToMono(byte[].class);
    }

    public Mono<byte[]> getThumbnailFromDropbox(final String path) {
        String dropboxApiArgs = "{\"format\":\"jpeg\",\"mode\":\"strict\",\"quality\":\"quality_80\",\"resource\":{\".tag\":\"path\",\"path\":\"" + path + "\"},\"size\":\"w64h64\"}";
        WebClient webClient = setupWebclient();
        return webClient.post()
                .uri(dropboxConfig.getThumbnailEndpoint())
                .headers(httpHeaders -> httpHeaders.addAll(createHeaders(dropboxApiArgs)))
                .retrieve()
                .bodyToMono(byte[].class);
    }

    public Mono<String> deleteFileFromDropbox(final String path) {
        String requestBody = "{\"path\":\"" + path + "\"}";
        WebClient webClient = WebClient.builder().build();

        return webClient.post()
                .uri(dropboxConfig.getDeleteEndpoint())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + dropboxConfig.getDropboxAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class);
    }

    private WebClient setupWebclient() {
        return webClientConfig.webClientBuilder().baseUrl(dropboxConfig.getBaseurl())
                .build();
    }

    private HttpHeaders createHeaders(final String dropBoxApiArgs) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + dropboxConfig.getDropboxAccessToken());
        headers.set("Dropbox-API-Arg", dropBoxApiArgs);
        return headers;
    }
}
