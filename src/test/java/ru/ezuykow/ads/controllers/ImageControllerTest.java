package ru.ezuykow.ads.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ezuykow.ads.services.ImageService;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    @Test
    public void getAvatarShouldOnlyCallGetUserAvatarMethodAndReturnBytes() {
        when(imageService.getUserAvatar(anyString())).thenReturn(new byte[0]);

        assertInstanceOf(byte[].class, imageController.getAvatar(" "));

        verify(imageService, only()).getUserAvatar(anyString());
    }

    @Test
    public void getAdImageShouldOnlyCallGetUserAvatarMethodAndReturnBytes() {
        when(imageService.getAdImage(anyString())).thenReturn(new byte[0]);

        assertInstanceOf(byte[].class, imageController.getAdImage(" "));

        verify(imageService, only()).getAdImage(anyString());
    }

}