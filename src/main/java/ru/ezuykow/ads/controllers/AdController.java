package ru.ezuykow.ads.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ezuykow.ads.dto.AdDto;
import ru.ezuykow.ads.dto.CreateAdDto;
import ru.ezuykow.ads.dto.FullAdDto;
import ru.ezuykow.ads.dto.ResponseWrapperAds;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.mappers.AdMapper;
import ru.ezuykow.ads.services.AdService;
import ru.ezuykow.ads.services.UserService;

import java.util.Optional;

/**
 * @author ezuykow
 */
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;
    private final UserService userService;
    private final AdMapper adMapper;

    @GetMapping("")
    public ResponseEntity<ResponseWrapperAds> getAllAds() {
        return ResponseEntity.ok(adMapper.mapAdsListToResponseWrapperAds(adService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullAdDto> getFullAdById(@PathVariable("id") int adId) {
        Optional<Ad> targetAdOpt = adService.findById(adId);
        return targetAdOpt.map(ad -> ResponseEntity.ok(adService.createFullAd(ad)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getMyAds(Authentication authentication) {
        User author = userService.findUserByEmail(authentication.getName());
        return ResponseEntity.ok(adMapper.mapAdsListToResponseWrapperAds(
                adService.findAllByAuthorId(author.getUserId())));
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDto> postAd(
            Authentication authentication,
            @RequestPart("image") MultipartFile image,
            @RequestPart("properties") CreateAdDto createAdsDto)
    {
        return ResponseEntity.ok(adService.createAd(authentication.getName(), image, createAdsDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdDto> editAd(
            Authentication authentication,
            @PathVariable("id") int adId,
            @RequestBody CreateAdDto createAdDto)
    {
        Optional<Ad> targetAdOpt = adService.findById(adId);
        if (targetAdOpt.isPresent()) {
            User author = userService.findById(targetAdOpt.get().getAuthor());
            if (authentication.getName().equals(author.getEmail())) {
                return ResponseEntity.ok(adService.editAd(targetAdOpt.get(), createAdDto));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> editAdImage(
            Authentication authentication,
            @PathVariable("id") int adId,
            @RequestPart("image") MultipartFile imageFile)
    {
        Optional<Ad> targetAdOpt = adService.findById(adId);
        if (targetAdOpt.isPresent()) {
            User author = userService.findById(targetAdOpt.get().getAuthor());
            if (authentication.getName().equals(author.getEmail())) {
                return ResponseEntity.ok().body(adService.editAdImage(targetAdOpt.get(), imageFile));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdById(@PathVariable("id") int adId) {
        Optional<Ad> targetAd = adService.findById(adId);
        return targetAd.map(ad -> {
                    adService.deleteById(adId);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }
}
