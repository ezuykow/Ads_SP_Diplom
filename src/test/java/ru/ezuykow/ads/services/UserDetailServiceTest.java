package ru.ezuykow.ads.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.entities.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class UserDetailServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailService userDetailService;

    @Test
    public void loadUserByUserNameShouldThrowExceptionWhenUserNotExisted() {
        when(userService.findUserByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername("email"));
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetails() {
        User testUser = new User();
        testUser.setEmail("email");
        testUser.setEncodedPassword("enPass");
        testUser.setRole(Role.USER);

        when(userService.findUserByEmail(testUser.getEmail())).thenReturn(testUser);

        UserDetails result = userDetailService.loadUserByUsername(testUser.getEmail());

        assertInstanceOf(UserDetails.class, result);
    }

}