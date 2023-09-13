package com.homeProject.JennsArtWebsite.controller;


import com.homeProject.JennsArtWebsite.service.DropboxService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/v1/dropbox")
public record DropboxController(DropboxService dropboxService) {

    @PostMapping("/upload")
    public Mono<ResponseEntity<String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return dropboxService.uploadFileToDropbox(file)
                .map(result -> ResponseEntity.ok("File uploaded successfully"))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed"));
    }

    @GetMapping("/download")
    public Mono<ResponseEntity<byte[]>> downloadFile(@RequestParam("path") String path) {
        return dropboxService.downloadFileFromDropbox(path)
                .map(fileContent -> fileContent != null
                        ? ResponseEntity.ok()
                            .header("Content-Disposition", "attachment; filename=" + path)
                            .body(fileContent)
                        : ResponseEntity.notFound().build());
    }

    @GetMapping("/thumbnail")
    public Mono<ResponseEntity<byte[]>> getThumbnail(@RequestParam("path") String path) {
        return dropboxService.getThumbnailFromDropbox(path)
                .map(thumbnailContent -> ResponseEntity.ok(thumbnailContent))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public Mono<String> deleteFile(@RequestBody String path) {
        return dropboxService.deleteFileFromDropbox(path);
    }
}
