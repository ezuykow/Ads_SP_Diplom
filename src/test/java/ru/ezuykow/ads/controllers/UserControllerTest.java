package ru.ezuykow.ads.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.ezuykow.ads.dto.UserDto;
import ru.ezuykow.ads.services.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @Test
    public void getMeShouldReturnStatusOkAndCallMethodGetUserDtoByEmail() {
        String testEmail = "email";
        when(authentication.getName()).thenReturn(testEmail);
        when(userService.findUserDtoByEmail(testEmail)).thenReturn(new UserDto());

        ResponseEntity<UserDto> response = userController.getMe(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(UserDto.class, response.getBody());
    }

    @Test
    public void setPasswordShouldReturnBadRequestWhenServiceReturnFalse() {
        when(authentication.getName()).thenReturn("");
        when(userService.setPassword(anyString(), any())).thenReturn(false);

        ResponseEntity<?> response = userController.setPassword(authentication, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void setPasswordShouldReturnOkWhenServiceReturnTrue() {
        when(authentication.getName()).thenReturn("");
        when(userService.setPassword(anyString(), any())).thenReturn(true);

        ResponseEntity<?> response = userController.setPassword(authentication, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void editUserShouldReturnOkWithUserDtoAndCallOnlyEditUserMethod() {
        when(authentication.getName()).thenReturn("");
        when(userService.editUser(anyString(), any())).thenReturn(new UserDto());

        ResponseEntity<UserDto> response = userController.editUser(authentication, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(UserDto.class, response.getBody());
        verify(userService, only()).editUser(anyString(), any());
    }

    @Test
    public void editImageShouldCallMethodUploadImageAndReturnOk() {
        when(authentication.getName()).thenReturn("");
        doNothing().when(userService).uploadImage(anyString(), any());

        ResponseEntity<?> response = userController.editImage(authentication, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, only()).uploadImage(anyString(), any());
    }

}