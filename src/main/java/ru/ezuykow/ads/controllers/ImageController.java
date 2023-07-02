package ru.ezuykow.ads.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ezuykow
 */
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    @Value("${avatars.dir.path}")
    private String avatarsDirPath;
    @Value("${ads.images.dir.path}")
    private String adsImagesDirPath;

    @GetMapping(value = "/avatars/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getAvatar(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Path.of(avatarsDirPath, fileName + ".png"));
    }

    @GetMapping(value = "/ads/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getAdImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Path.of(adsImagesDirPath, fileName + ".png"));
    }
}
