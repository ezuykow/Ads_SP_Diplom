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

    //-----------------API START-----------------

    /**
     * Return user's avatar
     * @param fileName name of avatar file
     * @return {@code byte[]} - bytes of avatar file
     * @author ezuykow
     */
    @GetMapping(value = "/avatars/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getAvatar(@PathVariable("fileName") String fileName) {
        return imageService.getUserAvatar(fileName);
    }

    /**
     * Return ad's image
     * @param fileName name of image file
     * @return {@code byte[]} - bytes of image file
     * @author ezuykow
     */
    @GetMapping(value = "/ads/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getAdImage(@PathVariable("fileName") String fileName) {
        return imageService.getAdImage(fileName);
    }

    //-----------------API END-----------------

}
