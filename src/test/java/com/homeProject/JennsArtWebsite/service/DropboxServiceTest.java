package com.homeProject.JennsArtWebsite.service;

import com.homeProject.JennsArtWebsite.config.DropboxConfig;
import com.homeProject.JennsArtWebsite.config.WebClientConfig;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class DropboxServiceTest {

    private DropboxService dropboxService;
    private MockWebServer mockWebServer;

    @Mock
    private DropboxConfig dropboxConfig;

    @BeforeEach
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClientConfig webClientConfig = new WebClientConfig();

        dropboxService = new DropboxService(dropboxConfig, webClientConfig);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void ShouldReturnOK_When_ResponseBodyIsCorrectInDownloadMethod() throws InterruptedException {
        byte[] expectedResponseBody = "Test file content".getBytes();
        when(dropboxConfig.getBaseurl()).thenReturn(mockWebServer.url("/").toString());
        when(dropboxConfig.getDownloadEndpoint()).thenReturn("/download");
        when(dropboxConfig.getDropboxAccessToken()).thenReturn("your_access_token");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new Buffer().write(expectedResponseBody)));

        byte[] actualResponseBody = dropboxService.downloadFileFromDropbox("/test/file.txt").block();
        assertArrayEquals(expectedResponseBody, actualResponseBody);
        assertEquals("/download", Objects.requireNonNull(mockWebServer.takeRequest().getPath()));
    }

    @Test
    public void ShouldReturnOK_When_ResponseBodyIsCorrectInThumbnailMethod() throws InterruptedException {
        byte[] expectedResponseBody = "Test thumbnail".getBytes();
        when(dropboxConfig.getBaseurl()).thenReturn(mockWebServer.url("/").toString());
        when(dropboxConfig.getThumbnailEndpoint()).thenReturn("/thumbnail");
        when(dropboxConfig.getDropboxAccessToken()).thenReturn("your_access_token");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new Buffer().write(expectedResponseBody)));

        byte[] actualResponseBody = dropboxService.getThumbnailFromDropbox("/test/example.jpg").block();
        assertArrayEquals(expectedResponseBody, actualResponseBody);
        assertEquals("/thumbnail", Objects.requireNonNull(mockWebServer.takeRequest().getPath()));
    }

    @Test
    public void ShouldReturnOK_When_UploadMethodIsUsedWithCorrectFile() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-file.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        when(dropboxConfig.getBaseurl()).thenReturn(mockWebServer.url("/").toString());
        when(dropboxConfig.getUploadEndpoint()).thenReturn("/upload");
        when(dropboxConfig.getDropboxAccessToken()).thenReturn("your_access_token");

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        Mono<Void> result = dropboxService.uploadFileToDropbox(multipartFile);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteFileFromDropbox_Success() {
        String pathToDelete = "/test/file.txt";
        when(dropboxConfig.getDeleteEndpoint()).thenReturn(mockWebServer.url("/").toString());
        when(dropboxConfig.getDropboxAccessToken()).thenReturn("your_access_token");

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        Mono<String> result = dropboxService.deleteFileFromDropbox(pathToDelete);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

    }
}

