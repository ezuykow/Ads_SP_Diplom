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

    //-----------------API START-----------------

    /**
     * Return all ads
     * @return {@link HttpStatus#OK} with all ads in {@link ResponseWrapperAds} instance
     * @author ezuykow
     */
    @GetMapping("")
    public ResponseEntity<ResponseWrapperAds> getAllAds() {
        return ResponseEntity.ok(adMapper.mapAdsListToResponseWrapperAds(adService.findAll()));
    }

    /**
     * Return ad with target {@code adId}
     * @param adId variable from URL
     * @return {@link HttpStatus#OK} with ad with {@code adId} in {@link FullAdDto} instance if existed, <br>
     * {@link HttpStatus#NOT_FOUND} if not existed
     * @author ezuykow
     */
    @GetMapping("/{id}")
    public ResponseEntity<FullAdDto> getFullAdById(@PathVariable("id") int adId) {
        Optional<Ad> targetAdOpt = adService.findById(adId);
        return targetAdOpt.map(ad -> ResponseEntity.ok(adService.createFullAd(ad)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Return ads of authorized user
     * @param authentication (inject) authorized user authentication info
     * @return {@link HttpStatus#OK} with all ads of authorized user in {@link ResponseWrapperAds} instance
     * @author ezuykow
     */
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getMyAds(Authentication authentication) {
        User author = userService.findUserByEmail(authentication.getName());
        return ResponseEntity.ok(adMapper.mapAdsListToResponseWrapperAds(author.getAds()));
    }

    /**
     * Create new ad
     * @param authentication (inject) authorized user authentication info
     * @param image {@link MultipartFile} with image
     * @param createAdsDto new ad info
     * @return {@link HttpStatus#OK} with added ad in {@link AdDto} instance
     * @author ezuykow
     */
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDto> postAd(
            Authentication authentication,
            @RequestPart("image") MultipartFile image,
            @RequestPart("properties") CreateAdDto createAdsDto)
    {
        return ResponseEntity.ok(adService.createAd(authentication.getName(), image, createAdsDto));
    }

    /**
     * Edit ad with target {@code adId}
     * @param authentication (inject) authorized user authentication info
     * @param adId variable from URL
     * @param createAdDto new ad info
     * @return {@link HttpStatus#OK} with edited ad in AdDto instance if target ad existed
     * and initiator is admin or author, <br>
     * {@link HttpStatus#NOT_FOUND} if target ad not existed, <br>
     * {@link HttpStatus#FORBIDDEN} if initiator not admin and not author
     * @author ezuykow
     */
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

    /**
     * Edit image of ad with target {@code adId}
     * @param authentication (inject) authorized user authentication info
     * @param adId variable from URL
     * @param imageFile {@link MultipartFile} with new image
     * @return {@link HttpStatus#OK} with edited ad if target ad existed and initiator is admin or author, <br>
     * {@link HttpStatus#NOT_FOUND} if target ad not existed, <br>
     * {@link HttpStatus#FORBIDDEN} if initiator not admin and not author
     * @author ezuykow
     */
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

    /**
     * Delete ad with target {@code adId}
     * @param authentication (inject) authorized user authentication info
     * @param adId variable from URL
     * @return {@link HttpStatus#OK} if target ad existed and initiator is admin or author, <br>
     * {@link HttpStatus#NOT_FOUND} if target ad not existed, <br>
     * {@link HttpStatus#FORBIDDEN} if initiator not admin and not author
     * @author ezuykow
     */
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

    //-----------------API END-----------------

    /**
     * Check that user is admin or ad's author
     * @param targetEmail target user email
     * @param targetAdOpt target ad (Optional)
     * @return {@code true} if user with {@code targetEmail} is admin or author of {@code targetAd}, <br>
     * {@code false} if not
     * @author ezuykow
     */
    private boolean isUserAdminOrAuthor(String targetEmail, Optional<Ad> targetAdOpt) {
        User initiator = userService.findUserByEmail(targetEmail);
        return initiator.getRole() == Role.ADMIN
                || initiator.getEmail()
                    .equals(targetAdOpt.orElseThrow(NonExistentAdException::new).getAuthor().getEmail());
    }
}
