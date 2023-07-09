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

    /**
     * Return bytes of user's avatar
     * @param fileName name of avatar file
     * @return {@code byte[]} - bytes of avatar file
     * @author ezuykow
     */
     @SneakyThrows
     public byte[] getUserAvatar(String fileName) {
         return Files.readAllBytes(Path.of(avatarsDirPath, fileName + fileNamePostfix));
     }

    /**
     * Return bytes of ad's image
     * @param fileName name of image file
     * @return {@code byte[]} - bytes of image file
     * @author ezuykow
     */
     @SneakyThrows
     public byte[] getAdImage(String fileName){
         return Files.readAllBytes(Path.of(adsImagesDirPath, fileName + fileNamePostfix));
     }

    /**
     * Upload ad's image
     * @param adId target ad's id
     * @param file {@link MultipartFile} with image
     * @return string with URL-tail to image
     * @author ezuykow
     */
     public String uploadAdImage(Integer adId, MultipartFile file) {
         Path filePath = Path.of(adsImagesDirPath, adId + fileNamePostfix);
         uploadImage(filePath, file);
         return buildURLTailToImage(adsImagesDirPath, adId.toString());
     }

    /**
     * Upload user's avatar
     * @param username target user's username (email)
     * @param file {@link MultipartFile} with avatar
     * @return string with URL-tail to avatar
     * @author ezuykow
     */
     public String uploadUserAvatar(String username, MultipartFile file) {
         Path filePath = Path.of(avatarsDirPath, username + fileNamePostfix);
         uploadImage(filePath, file);
         return buildURLTailToImage(avatarsDirPath, username);
     }

    /**
     * Delete ad's image
     * @param adId target ad's id
     * @author ezuykow
     */
     @SneakyThrows
     public void deleteAdImage(int adId) {
         Files.deleteIfExists(Path.of(adsImagesDirPath, adId + fileNamePostfix));
     }

    //-----------------API END-----------------

    /**
     * Upload image file to filesystem
     * @param path file's path
     * @param file {@link MultipartFile} target image file
     * @author ezuykow
     */
    @SneakyThrows
    private void uploadImage(Path path, MultipartFile file) {
        Files.createDirectories(path.getParent());
        Files.deleteIfExists(path);
        file.transferTo(path);
    }

    /**
     * Build string with URL-tail to target file
     * @param dir file directory
     * @param fileName name of file
     * @return string with URL-tail to target file
     * @author ezuykow
     */
    private String buildURLTailToImage(String dir, String fileName) {
         return "/" + dir + "/" + fileName;
    }
}
