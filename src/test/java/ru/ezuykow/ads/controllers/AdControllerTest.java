package ru.ezuykow.ads.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.ezuykow.ads.dto.AdDto;
import ru.ezuykow.ads.dto.FullAdDto;
import ru.ezuykow.ads.dto.ResponseWrapperAds;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.mappers.AdMapper;
import ru.ezuykow.ads.services.AdService;
import ru.ezuykow.ads.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class AdControllerTest {

    @Mock
    private AdService adService;
    @Mock
    private Authentication authentication;
    @Mock
    private UserService userService;

    @Spy
    private AdMapper adMapper;

    @InjectMocks
    private AdController adController;

    @Test
    public void getAllAdsShouldReturnResponseEntityWithResponseWrapperAdsInside() {
        Ad testAd = new Ad(0, new User(), "image", 123, "title", "desc", null);
        List<Ad> testAdsList = List.of(testAd);

        when(adService.findAll()).thenReturn(testAdsList);

        ResponseEntity<ResponseWrapperAds> response = adController.getAllAds();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getCount(), testAdsList.size());
    }

    @Test
    public void getFullAdByIdShouldReturnNotFoundWhenAdNotExist() {
        int testId = 1;

        when(adService.findById(testId)).thenReturn(Optional.empty());

        ResponseEntity<FullAdDto> response = adController.getFullAdById(testId);

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void getFullAdByIdShouldReturnResponseEntityWithFullAdDto() {
        Ad testAd = new Ad(10, new User(), "image", 123, "title", "desc", null);
        FullAdDto testFAD = adMapper.mapAdAndAuthorToFullAdDto(testAd, testAd.getAuthor());
        int testId = testAd.getPk();

        when(adService.findById(testId)).thenReturn(Optional.of(testAd));
        when(adService.createFullAd(testAd)).thenReturn(testFAD);

        ResponseEntity<FullAdDto> response = adController.getFullAdById(testId);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getPk(), testId);
    }

    @Test
    public void getMyAdsShouldReturnResponseEntityWithResponseWrapperAds() {
        Ad testAd = new Ad(
                0, new User(), "image", 123, "title", "desc", null);
        List<Ad> testAdsList = List.of(testAd);
        User testAuthor = new User(
                "email", "fn", "ln", "phone", Role.USER, "image",
                "password", testAdsList, null);
        testAd.setAuthor(testAuthor);

        when(authentication.getName()).thenReturn(testAuthor.getEmail());
        when(userService.findUserByEmail(testAuthor.getEmail())).thenReturn(testAuthor);

        ResponseEntity<ResponseWrapperAds> response = adController.getMyAds(authentication);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getCount(), testAdsList.size());
    }

    @Test
    public void postAdShouldCallCreateAdMethod() {
        when(adService.createAd(any(), any(), any())).thenReturn(null);

        ResponseEntity<AdDto> response = adController.postAd(authentication, null, null);

        verify(adService, only()).createAd(any(), any(), any());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void editAdShouldReturnNotFoundWhenAdNotExisted() {
        when(adService.findById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<AdDto> response = adController.editAd(null, 1, null);

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void editAdShouldReturnForbiddenWhenInitiatorNotAdminNotAuthor() {
        Ad testAd = new Ad(
                10, new User(), "image", 123, "title", "desc", null);
        User testAuthor = new User(
                "email", "fn", "ln", "phone", Role.USER, "image",
                "password", List.of(testAd), null);
        User testNotAuthor = new User(
                "notAuthor", "fn", "ln", "phone", Role.USER, "image",
                "password", null, null);
        testAd.setAuthor(testAuthor);
        Optional<Ad> targetAdOptTest = Optional.of(testAd);

        when(adService.findById(testAd.getPk())).thenReturn(targetAdOptTest);
        when(authentication.getName()).thenReturn(testNotAuthor.getEmail());
        when(userService.findUserByEmail(testNotAuthor.getEmail())).thenReturn(testNotAuthor);

        ResponseEntity<AdDto> response = adController.editAd(authentication, testAd.getPk(), null);

        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void editAdShouldReturnResponseEntityWithAdDto() {
        Ad testAd = new Ad(
                10, new User(), "image", 123, "title", "desc", null);
        User testAuthor = new User(
                "email", "fn", "ln", "phone", Role.USER, "image",
                "password", List.of(testAd), null);
        testAd.setAuthor(testAuthor);
        Optional<Ad> targetAdOptTest = Optional.of(testAd);

        when(adService.findById(testAd.getPk())).thenReturn(targetAdOptTest);
        when(authentication.getName()).thenReturn(testAuthor.getEmail());
        when(userService.findUserByEmail(testAuthor.getEmail())).thenReturn(testAuthor);
        when(adService.editAd(any(), any())).thenReturn(new AdDto());

        ResponseEntity<AdDto> response = adController.editAd(authentication, testAd.getPk(), null);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertInstanceOf(AdDto.class, response.getBody());
    }

    @Test
    public void editAdImageShouldReturnNotFoundWhenAdNotExisted() {
        when(adService.findById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<?> response = adController.editAdImage(null, 1, null);

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void editAdImageShouldReturnForbiddenWhenInitiatorNotAdminNotAuthor() {
        Ad testAd = new Ad(
                10, new User(), "image", 123, "title", "desc", null);
        User testAuthor = new User(
                "email", "fn", "ln", "phone", Role.USER, "image",
                "password", List.of(testAd), null);
        User testNotAuthor = new User(
                "notAuthor", "fn", "ln", "phone", Role.USER, "image",
                "password", null, null);
        testAd.setAuthor(testAuthor);
        Optional<Ad> targetAdOptTest = Optional.of(testAd);

        when(adService.findById(testAd.getPk())).thenReturn(targetAdOptTest);
        when(authentication.getName()).thenReturn(testNotAuthor.getEmail());
        when(userService.findUserByEmail(testNotAuthor.getEmail())).thenReturn(testNotAuthor);

        ResponseEntity<?> response = adController.editAdImage(authentication, testAd.getPk(), null);

        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void editAdImageShouldReturnResponseEntityWithByteArray() {
        Ad testAd = new Ad(
                10, new User(), "image", 123, "title", "desc", null);
        User testAuthor = new User(
                "email", "fn", "ln", "phone", Role.USER, "image",
                "password", List.of(testAd), null);
        testAd.setAuthor(testAuthor);
        Optional<Ad> targetAdOptTest = Optional.of(testAd);

        when(adService.findById(testAd.getPk())).thenReturn(targetAdOptTest);
        when(authentication.getName()).thenReturn(testAuthor.getEmail());
        when(userService.findUserByEmail(testAuthor.getEmail())).thenReturn(testAuthor);
        when(adService.editAdImage(any(), any())).thenReturn(new byte[0]);

        ResponseEntity<?> response = adController.editAdImage(authentication, testAd.getPk(), null);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertInstanceOf(byte[].class, response.getBody());
    }

    @Test
    public void deleteAdByIdShouldReturnNotFoundWhenAdNotExisted() {
        when(adService.findById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<?> response = adController.deleteAdById(null, 1);

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteAdByIdShouldReturnForbiddenWhenInitiatorNotAdminNotAuthor() {
        Ad testAd = new Ad(
                10, new User(), "image", 123, "title", "desc", null);
        User testAuthor = new User(
                "email", "fn", "ln", "phone", Role.USER, "image",
                "password", List.of(testAd), null);
        User testNotAuthor = new User(
                "notAuthor", "fn", "ln", "phone", Role.USER, "image",
                "password", null, null);
        testAd.setAuthor(testAuthor);
        Optional<Ad> targetAdOptTest = Optional.of(testAd);

        when(adService.findById(testAd.getPk())).thenReturn(targetAdOptTest);
        when(authentication.getName()).thenReturn(testNotAuthor.getEmail());
        when(userService.findUserByEmail(testNotAuthor.getEmail())).thenReturn(testNotAuthor);

        ResponseEntity<?> response = adController.deleteAdById(authentication, testAd.getPk());

        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void editAdImageShouldCallDeleteByIdMethodAndReturnResponseEntityWithStatusOk() {
        Ad testAd = new Ad(
                10, new User(), "image", 123, "title", "desc", null);
        User testAuthor = new User(
                "email", "fn", "ln", "phone", Role.USER, "image",
                "password", List.of(testAd), null);
        testAd.setAuthor(testAuthor);
        Optional<Ad> targetAdOptTest = Optional.of(testAd);

        when(adService.findById(testAd.getPk())).thenReturn(targetAdOptTest);
        when(authentication.getName()).thenReturn(testAuthor.getEmail());
        when(userService.findUserByEmail(testAuthor.getEmail())).thenReturn(testAuthor);
        doNothing().when(adService).deleteById(testAd.getPk());

        ResponseEntity<?> response = adController.deleteAdById(authentication, testAd.getPk());

        verify(adService, times(1)).deleteById(testAd.getPk());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}