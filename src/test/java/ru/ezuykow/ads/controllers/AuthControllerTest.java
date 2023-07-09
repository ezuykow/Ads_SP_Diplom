package ru.ezuykow.ads.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.ezuykow.ads.dto.LoginReq;
import ru.ezuykow.ads.dto.RegisterReq;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.services.AuthService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    public void loginShouldReturnUnauthorizedStatusThenServiceReturnFalse() {
        LoginReq testLogReq = new LoginReq();

        when(authService.login(testLogReq.getUsername(), testLogReq.getPassword())).thenReturn(false);

        ResponseEntity<?> response = authController.login(testLogReq);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void loginShouldReturnStatusOkThenServiceReturnTrue() {
        LoginReq testLogReq = new LoginReq();

        when(authService.login(testLogReq.getUsername(), testLogReq.getPassword())).thenReturn(true);

        ResponseEntity<?> response = authController.login(testLogReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void registerShouldReturnStatusBadRequestWhenServiceReturnFalse() {
        RegisterReq testRegReq = new RegisterReq();

        when(authService.register(testRegReq, Role.USER)).thenReturn(false);

        ResponseEntity<?> response = authController.register(testRegReq);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void registerShouldReturnStatusCreatedWhenServiceReturnTrue() {
        RegisterReq testRegReq = new RegisterReq();

        when(authService.register(testRegReq, Role.USER)).thenReturn(true);

        ResponseEntity<?> response = authController.register(testRegReq);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}