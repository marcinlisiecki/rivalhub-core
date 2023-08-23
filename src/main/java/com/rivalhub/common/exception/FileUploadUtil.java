package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;
import com.rivalhub.organization.Organization;
import io.github.classgraph.Resource;
import jakarta.mail.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FileUploadUtil {
    @Value("${app.organization.img.catalog}")
    private String organizationImgCatalog;

    private void saveFile(String uploadDir, String fileName,
                                MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException(ErrorMessages.COULD_NOT_SAVE_FILE + ": " + fileName);
        }
    }

    public String saveOrganizationImage(MultipartFile multipartFile, Organization organization){
        String fileName = "avatar" + multipartFile.getOriginalFilename()
                .substring(multipartFile.getOriginalFilename().lastIndexOf("."));

        String uploadDir = organizationImgCatalog + organization.getId();
        try {
            saveFile(uploadDir, fileName, multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uploadDir + "/" + fileName;
    }


}