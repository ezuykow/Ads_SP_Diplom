package ru.ezuykow.ads.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ezuykow
 */
@Service
public class ImageService {

    @Value("${avatars.dir.path}")
    private String avatarsDirPath;
    @Value("${ads.images.dir.path}")
    private String adsImagesDirPath;
    @Value("${image.file.name.postfix}")
    private String fileNamePostfix;


    //-----------------API START-----------------

     @SneakyThrows
     public byte[] getUserAvatar(String fileName) {
         return Files.readAllBytes(Path.of(avatarsDirPath, fileName + fileNamePostfix));
     }

     @SneakyThrows
     public byte[] getAdImage(String fileName){
         return Files.readAllBytes(Path.of(adsImagesDirPath, fileName + fileNamePostfix));
     }

     public String uploadAdImage(Integer adId, MultipartFile file) {
         Path filePath = Path.of(adsImagesDirPath, adId + fileNamePostfix);
         uploadImage(filePath, file);
         return buildURLTailToImage(adsImagesDirPath, adId.toString());
     }

     public String uploadUserAvatar(String username, MultipartFile file) {
         Path filePath = Path.of(avatarsDirPath, username + fileNamePostfix);
         uploadImage(filePath, file);
         return buildURLTailToImage(avatarsDirPath, username);
     }

     @SneakyThrows
     public void deleteAdImage(int adId) {
         Files.deleteIfExists(Path.of(adsImagesDirPath, adId + fileNamePostfix));
     }

    //-----------------API END-----------------

    @SneakyThrows
    private void uploadImage(Path path, MultipartFile file) {
        Files.createDirectories(path.getParent());
        Files.deleteIfExists(path);
        file.transferTo(path);
    }

    private String buildURLTailToImage(String dir, String fileName) {
         return "/" + dir + "/" + fileName;
    }
}
