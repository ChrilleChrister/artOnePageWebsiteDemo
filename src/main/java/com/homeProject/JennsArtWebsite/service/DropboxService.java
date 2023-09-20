package com.homeProject.JennsArtWebsite.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.homeProject.JennsArtWebsite.client.DropboxClient;
import com.homeProject.JennsArtWebsite.entity.ImageMetadata;
import com.homeProject.JennsArtWebsite.mapper.ImageMetadataMapper;
import com.homeProject.JennsArtWebsite.repository.ImageMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class DropboxService {

    private final DropboxClient dropboxClient;
    private final ImageMetadataMapper imageMetadataMapper;
    private final ImageMetadataRepository imageMetadataRepository;


    public void downloadFile(final String filePath) throws IOException, DbxException {
        DbxClientV2 client = dropboxClient.getDropboxClient();
        try {
            client.files().downloadBuilder(filePath)
                    .download(new FileOutputStream(getUserDownloadPath(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("File download failed due to IO error" + e.getMessage(), e);
        } catch (DbxException e) {
            e.printStackTrace();
            throw new DbxException("File download failed due to Dropbox API error: " + e.getMessage(), e);
        }
    }

    public void uploadFile(final MultipartFile file, final String path) throws IOException, DbxException {
        DbxClientV2 client = dropboxClient.getDropboxClient();
        try {
            client.files()
                    .uploadBuilder(path)
                    .uploadAndFinish(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("File upload failed due to IO error: " + e.getMessage(), e);
        } catch (DbxException e) {
            e.printStackTrace();
            throw new DbxException("File upload failed due to Dropbox API error: " + e.getMessage(), e);
        }
    }

    public void deleteFile(final String path) throws DbxException {
        DbxClientV2 client = dropboxClient.getDropboxClient();
        try {
            client.files().deleteV2(path);
        } catch (DbxException e) {
            e.printStackTrace();
            throw new DbxException("File deletion failed due to Dropbox API error: " + e.getMessage(), e);
        }
    }

    public String getImageUrl(Long id) {
        return imageMetadataRepository.selectImageById(id)
                .map(ImageMetadata::getImageUrl)
                .orElseThrow(() -> new RuntimeException(
                        "customer with id [%s] not found".formatted(id)
                ));
    }


    private String getUserDownloadPath(final String path) {
        String defaultLocalDirectory = "Downloads";
        String userHome = System.getProperty("user.home");
        String fileName = new File(path).getName();
        return userHome + File.separator + defaultLocalDirectory + File.separator + fileName;
    }
}


