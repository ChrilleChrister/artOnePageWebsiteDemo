package com.homeProject.JennsArtWebsite.controller;


import com.homeProject.JennsArtWebsite.service.DropboxService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/v1/dropbox")
@AllArgsConstructor
public class DropboxController {

    private final DropboxService dropboxService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        boolean upLoadSuccessful = dropboxService.uploadFileToDropbox(file);
        return upLoadSuccessful
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }


    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("path") String path) {
        byte[] fileContent = dropboxService.downloadFileFromDropbox(path);
        return fileContent != null
                ? ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + path)
                    .body(fileContent)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/thumbnail")
    public ResponseEntity<byte[]> getThumbnail(@RequestParam("path") String path) {
        byte[] thumbnailContent = dropboxService.getThumbnailFromDropbox(path);
        return thumbnailContent != null
                ? ResponseEntity.ok(thumbnailContent)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("path") String path) {
        boolean deletionSuccessful = dropboxService.deleteFileFromDropbox(path);
        return deletionSuccessful
                ? ResponseEntity.ok("File deleted from Dropbox.")
                : ResponseEntity.badRequest().body("Failed to delete file from Dropbox.");
    }
}
