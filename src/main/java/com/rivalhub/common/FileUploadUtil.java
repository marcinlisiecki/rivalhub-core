package com.rivalhub.common;

import com.rivalhub.organization.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FileUploadUtil {
    @Value("${app.organization.img.catalog}")
    private String organizationImgCatalog;

    @Value("${app.organization.img.name}")
    private String organizationImgName;


    public String saveOrganizationImage(MultipartFile multipartFile, Organization organization){
        String fileName = createFileName(multipartFile).toLowerCase();
        String uploadDir = getUploadDir(organization, multipartFile);

        try {
            saveFile(uploadDir, fileName, multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uploadDir;
    }

    public void updateOrganizationImage(MultipartFile multipartFile, Organization organization) {
        String fileName = createFileName(multipartFile);
        String uploadDir = getUploadDir(organization, multipartFile);

        if (organization.getImageUrl() != null) deleteFile(uploadDir);
        try {
            saveFile(uploadDir, fileName, multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        organization.setImageUrl(uploadDir + "/" + fileName);
    }

    private String getUploadDir(Organization organization, MultipartFile multipartFile) {
        if (organization.getImageUrl() == null)
            return organizationImgCatalog + organization.getName() + LocalDateTime.now().toString().replace(":", "-");

        String avatarUrl =  organizationImgName + multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        return organization.getImageUrl().replace("/" + avatarUrl, "");
    }

    private void deleteFile(String uploadDir){
        Path imagesPath = Path.of(uploadDir);

        try {
            Files.delete(imagesPath);
            System.out.println("File "
                    + imagesPath.toAbsolutePath()
                    + " successfully removed");
        } catch (IOException e) {
            System.err.println("Unable to delete "
                    + imagesPath.toAbsolutePath()
                    + " due to...");
            e.printStackTrace();
        }
    }

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

    private String createFileName(MultipartFile multipartFile) {
        return organizationImgName + multipartFile.getOriginalFilename()
                .substring(multipartFile.getOriginalFilename().lastIndexOf("."));
    }
}
