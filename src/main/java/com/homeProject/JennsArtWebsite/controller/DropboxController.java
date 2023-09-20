package com.homeProject.JennsArtWebsite.controller;

import com.dropbox.core.DbxException;
import com.homeProject.JennsArtWebsite.service.DropboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DropboxController {

    private final DropboxService dropboxService;


    @GetMapping
    public String getImageUrl(@PathVariable("id") Long id ){
        return dropboxService.getImageUrl(id);
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestParam String path) {
        try {
            dropboxService.downloadFile(path);
            return ResponseEntity.ok("File downloaded successfully!");
        } catch (IOException | DbxException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading file from Dropbox: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("path") String path) {
        try {
            dropboxService.uploadFile(file, path);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException | DbxException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading file to Dropbox");
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestBody String path) {
        try {
            dropboxService.deleteFile(path);
            return ResponseEntity.ok("File deleted successfully");
        } catch (DbxException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting file from Dropbox");
        }
    }


}
