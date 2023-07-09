package ru.ezuykow.ads.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ezuykow.ads.dto.RegisterReq;
import ru.ezuykow.ads.dto.Role;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordEncoder encoder;
    @Mock
    private UserService userService;
    @Mock
    private UserDetailService userDetailService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void loginShouldReturnTrueIfUserSuccessfullyLogin() {
        UserDetails ud = org.springframework.security.core.userdetails.User.builder()
                .username("email")
                .password("pass")
                .roles("USER")
                .build();
        when(userDetailService.loadUserByUsername("email")).thenReturn(ud);
        when(encoder.matches(any(), any())).thenReturn(true);

        boolean result = authService.login("email", "pass");

        assertTrue(result);
    }

    @Test
    public void registerShouldReturnFalseIfUserAlreadyExist() {
        RegisterReq req = new RegisterReq();
        req.setUsername("email");

        when(userService.findUserByEmail(req.getUsername())).thenReturn(new ru.ezuykow.ads.entities.User());

        boolean result = authService.register(req, Role.USER);

        assertFalse(result);
    }

    @Test
    public void registerShouldReturnTrueIfUserSuccessfullyRegistered() {
        RegisterReq req = new RegisterReq();
        req.setUsername("email");
        req.setPassword("pass");

        when(userService.findUserByEmail(req.getUsername())).thenReturn(null);
        when(encoder.encode(req.getPassword())).thenReturn("enPass");
        doNothing().when(userService).saveUserFromRegReq(req, Role.USER, "enPass");

        boolean result = authService.register(req, Role.USER);

        assertTrue(result);
    }
}