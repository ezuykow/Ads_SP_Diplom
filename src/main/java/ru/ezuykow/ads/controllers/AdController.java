package ru.ezuykow.ads.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ezuykow.ads.dto.*;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.exceptions.NonExistentAdException;
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
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getMyAds(Authentication authentication) {
        User author = userService.findUserByEmail(authentication.getName());
        return ResponseEntity.ok(adMapper.mapAdsListToResponseWrapperAds(author.getAds()));
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
           User initiator = userService.findUserByEmail(authentication.getName());
           if (initiator.getRole() == Role.ADMIN
                   || initiator.getEmail().equals(targetAdOpt.get().getAuthor().getEmail())) {
               return ResponseEntity.ok(adService.editAd(targetAdOpt.get(), createAdDto));
           }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
            if (isUserAdminOrAuthor(authentication.getName(), targetAdOpt)) {
                return ResponseEntity.ok().body(adService.editAdImage(targetAdOpt.get(), imageFile));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdById(
            Authentication authentication,
            @PathVariable("id") int adId)
    {
        Optional<Ad> targetAdOpt = adService.findById(adId);
        if (targetAdOpt.isPresent()) {
            if (isUserAdminOrAuthor(authentication.getName(), targetAdOpt)){
                adService.deleteById(adId);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private boolean isUserAdminOrAuthor(String targetEmail, Optional<Ad> targetAdOpt) {
        User initiator = userService.findUserByEmail(targetEmail);
        return initiator.getRole() == Role.ADMIN
                || initiator.getEmail()
                    .equals(targetAdOpt.orElseThrow(NonExistentAdException::new).getAuthor().getEmail());
    }
}
