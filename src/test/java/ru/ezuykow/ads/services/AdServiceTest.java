package ru.ezuykow.ads.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ezuykow.ads.dto.AdDto;
import ru.ezuykow.ads.dto.CreateAdDto;
import ru.ezuykow.ads.dto.FullAdDto;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.mappers.AdMapper;
import ru.ezuykow.ads.repositories.AdRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock
    private AdRepository adRepository;
    @Mock
    private UserService userService;
    @Mock
    private ImageService imageService;

    @Spy
    private AdMapper adMapper;

    @InjectMocks
    private AdService adService;

    @Test
    public void findAllShouldCallOnlyRepositoryMethFindAll() {
        when(adRepository.findAll()).thenReturn(new ArrayList<>(0));
        adService.findAll();
        verify(adRepository, only()).findAll();
    }

    @Test
    public void findByIdShouldCallRepositoryMethFindById() {
        int testId = 10;
        when(adRepository.findById(testId)).thenReturn(Optional.empty());
        adService.findById(testId);
        verify(adRepository, only()).findById(testId);
    }

    @Test
    public void createAdShouldReturnCreatedAdDto() {
        CreateAdDto testCAD = new CreateAdDto("desc", 123, "tit");
        Ad testAd = adMapper.mapCreateAdDtoToAd(testCAD);
        User author = new User();
        testAd.setAuthor(author);
        testAd.setPk(10);

        when(userService.findUserByEmail(anyString())).thenReturn(new User());
        when(adRepository.save(any())).thenReturn(testAd);
        when(imageService.uploadAdImage(any(), any())).thenReturn("imagePath");

        AdDto result = adService.createAd("email", null, testCAD);

        assertEquals(testAd.getPk(), result.getPk());
    }

    @Test
    public void createFullAdShouldReturnFullAdDto() {
        Ad testAd = new Ad(10, null, "im", 123, "tit", "desc", null);
        User author = new User(10, "email", "fn", "ln", "p", Role.USER,
                "im", "pass", List.of(testAd), null);
        testAd.setAuthor(author);
        FullAdDto expected = adMapper.mapAdAndAuthorToFullAdDto(testAd, author);

        FullAdDto result = adService.createFullAd(testAd);

        assertEquals(expected, result);
    }

    @Test
    public void editAdShouldReturnEditedAdDto() {
        Ad testAd = new Ad(10, null, "im", 123, "tit", "desc", null);
        User author = new User(10, "email", "fn", "ln", "p", Role.USER,
                "im", "pass", List.of(testAd), null);
        testAd.setAuthor(author);
        CreateAdDto testCAD = new CreateAdDto("d", 321, "t");

        Ad editedAd = new Ad(10, author, "im", 321, "t", "d", null);
        AdDto expected = adMapper.mapEntityToDto(editedAd);

        when(adRepository.save(any())).thenReturn(any());

        AdDto result = adService.editAd(testAd, testCAD);

        assertEquals(expected, result);
    }

    @Test
    public void editAdImageShouldCallGetAdImageMethod() {
        Ad testAd = new Ad(10, null, "im", 123, "tit", "desc", null);

        when(imageService.uploadAdImage(any(), any())).thenReturn("");
        when(imageService.getAdImage(anyString())).thenReturn(new byte[0]);

        adService.editAdImage(testAd, null);

        verify(imageService, times(1)).getAdImage(anyString());
    }

    @Test
    public void saveShouldCallRepositoryMethSave() {
        when(adRepository.save(any())).thenReturn(null);

        adService.save(null);

        verify(adRepository, only()).save(any());
    }

    @Test
    public void deleteByIdShouldCallDeleteImageAndDeleteById() {
        doNothing().when(imageService).deleteAdImage(anyInt());
        doNothing().when(adRepository).deleteById(anyInt());

        adService.deleteById(anyInt());

        verify(imageService, times(1)).deleteAdImage(anyInt());
        verify(adRepository, times(1)).deleteById(anyInt());
    }
}