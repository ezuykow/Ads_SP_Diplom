package ru.ezuykow.ads.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.ezuykow.ads.services.ImageService;

/**
 * @author ezuykow
 */
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping(value = "/avatars/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getAvatar(@PathVariable("fileName") String fileName) {
        return imageService.getUserAvatar(fileName);
    }

    @GetMapping(value = "/ads/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getAdImage(@PathVariable("fileName") String fileName) {
        return imageService.getAdImage(fileName);
    }
}
