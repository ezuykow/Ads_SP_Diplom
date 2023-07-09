package ru.ezuykow.ads.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Test
    public void getUserAvatarsShouldReturnBytes() {
        try (MockedStatic<Files> files = mockStatic(Files.class);
        MockedStatic<Path> path = mockStatic(Path.class)) {

            path.when(() -> Path.of(anyString()))
                    .thenReturn(null);
            files.when(() -> Files.readAllBytes(any()))
                    .thenReturn(new byte[0]);

            assertInstanceOf(byte[].class, imageService.getUserAvatar("fn"));
        }
    }

    @Test
    public void getAdImageShouldReturnBytes() {
        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedStatic<Path> path = mockStatic(Path.class)) {

            path.when(() -> Path.of(anyString()))
                    .thenReturn(null);
            files.when(() -> Files.readAllBytes(any()))
                    .thenReturn(new byte[0]);

            assertInstanceOf(byte[].class, imageService.getAdImage("fn"));
        }
    }
}