package com.delivery.app.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DeliciasFileUtils {

    //@Value("${delicias.files.upload.path}")
    private Path fileStorageLocation;

    public String saveFile(MultipartFile file) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path targetLocation = this.fileStorageLocation.resolve(fileName);

        verifyDirectoryExistence(targetLocation);
        Files.copy(file.getInputStream(), targetLocation);

        return fileName;
    }



    public void deleteFile(String fileName) {
        try {
            Files.deleteIfExists(fileStorageLocation.resolve(fileName).normalize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void verifyDirectoryExistence(Path targetLocation) throws IOException {
        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        }
    }

}
