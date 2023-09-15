package com.homeProject.JennsArtWebsite.controller;

import com.homeProject.JennsArtWebsite.service.DropboxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DropboxControllerTest {


    @Mock
    private DropboxService dropboxService;
    @InjectMocks
    private DropboxController dropboxController;

    @Test
    public void ShouldReturnSuccess_When_UploadMethodSucceeds() throws IOException {
        when(dropboxService.uploadFileToDropbox(any(MultipartFile.class)))
                .thenReturn(Mono.empty());

        MultipartFile file = mock(MultipartFile.class);

        StepVerifier.create(dropboxController.uploadFile(file))
                .expectComplete()
                .verify();

        verify(dropboxService, times(1)).uploadFileToDropbox(eq(file));
    }

    @Test
    public void ShouldReturnError_When_UploadMethodFails() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(dropboxService.uploadFileToDropbox(file)).thenReturn(Mono.error(new IOException("Simulated error")));

        StepVerifier.create(dropboxController.uploadFile(file))
                .expectNextMatches(entity -> {
                    HttpStatusCode statusCode = entity.getStatusCode();
                    String responseBody = entity.getBody();
                    return statusCode == HttpStatus.INTERNAL_SERVER_ERROR &&
                            responseBody.equals("Upload failed");
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void ShouldReturnSuccess_When_DownloadMethodSucceeds() {
        byte [] mockByteArray = new byte[]{1, 2, 3, 4, 5};
        String mockString = "mockString";
        when(dropboxService.downloadFileFromDropbox(mockString)).thenReturn(Mono.justOrEmpty(mockByteArray));

        StepVerifier.create(dropboxController.downloadFile(mockString))
                .expectNextMatches(entity -> {
                    HttpStatusCode statusCode = entity.getStatusCode();
                    byte[] responseBody = entity.getBody();
                    return statusCode == HttpStatus.OK &&
                            responseBody != null;
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void ShouldDeleteFile_When_DeleteFileMethodSucceeds() {
        String mockPath = "mockfile.jpg";
        String expectedResponse = "File deleted successfully";

        when(dropboxService.deleteFileFromDropbox(mockPath))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(dropboxController.deleteFile(mockPath))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    public void ShouldDownloadThumbnail_When_GetThumbnailMethodsSucceeds() {

        String mockPath = "mockJpg.jpg";
        byte[] expectedThumbnailContent = "thumbnail-content".getBytes();

        when(dropboxService.getThumbnailFromDropbox(mockPath)).thenReturn(Mono.just(expectedThumbnailContent));

        StepVerifier.create(dropboxController.getThumbnail(mockPath))
                .expectNext(ResponseEntity.ok(expectedThumbnailContent))
                .verifyComplete();

    }


}


