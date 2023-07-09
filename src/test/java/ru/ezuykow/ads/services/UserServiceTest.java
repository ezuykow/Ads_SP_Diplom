package ru.ezuykow.ads.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ezuykow.ads.dto.NewPassword;
import ru.ezuykow.ads.dto.RegisterReq;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.dto.UserDto;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.mappers.UserMapper;
import ru.ezuykow.ads.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private ImageService imageService;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void setPasswordShouldReturnFalseIfCurrentPasswordIsIncorrect() {
        User testUser = new User();
        testUser.setEncodedPassword("enPass");

        NewPassword testNP = new NewPassword();
        testNP.setCurrentPassword("current");

        when(repository.findUserByEmail(anyString())).thenReturn(testUser);
        when(encoder.matches(testNP.getCurrentPassword(), testUser.getEncodedPassword()))
                .thenReturn(false);

        assertFalse(userService.setPassword("email", testNP));
    }

    @Test
    public void setPasswordShouldReturnTrue() {
        User testUser = new User();
        testUser.setEncodedPassword("enPass");

        NewPassword testNP = new NewPassword();
        testNP.setCurrentPassword("current");
        testNP.setNewPassword("new");

        when(repository.findUserByEmail(anyString())).thenReturn(testUser);
        when(encoder.matches(testNP.getCurrentPassword(), testUser.getEncodedPassword()))
                .thenReturn(true);
        when(encoder.encode(testNP.getNewPassword())).thenReturn("ep");
        when(repository.save(any())).thenReturn(null);

        assertTrue(userService.setPassword("email", testNP));
    }

    @Test
    public void findUserDtoByEmailShouldReturnUserDto() {
        User testUser = new User(1, "e", "f", "l", "p", Role.USER,
                "i", "ep", null, null);

        when(repository.findUserByEmail(testUser.getEmail())).thenReturn(testUser);

        UserDto result = userService.findUserDtoByEmail(testUser.getEmail());

        assertInstanceOf(UserDto.class, result);
    }

    @Test
    public void saveShouldOnlyCallRepositorySave() {
        when(repository.save(any())).thenReturn(null);

        userService.save(null);

        verify(repository, only()).save(any());
    }

    @Test
    public void saveUserByRegReqShouldCallRepositorySave() {
        RegisterReq testRR = new RegisterReq();
        testRR.setPassword("p");
        testRR.setUsername("u");
        testRR.setPhone("p");
        testRR.setRole(Role.USER);
        testRR.setLastName("l");
        testRR.setFirstName("f");

        when(repository.save(any())).thenReturn(null);

        userService.saveUserFromRegReq(testRR, Role.USER, "enPass");

        verify(repository, times(1)).save(any());
    }

    @Test
    public void editUserShouldReturnEditedUserDto() {
        User testUser = new User(1, "e", "f", "l", "p", Role.USER,
                "i", "ep", null, null);
        UserDto testUD = new UserDto();
        testUD.setFirstName("fn");
        testUD.setLastName("ln");
        testUD.setPhone("ph");

        when(repository.findUserByEmail(testUser.getEmail())).thenReturn(testUser);
        when(repository.save(any())).thenReturn(null);

        UserDto result = userService.editUser(testUser.getEmail(), testUD);

        assertInstanceOf(UserDto.class, result);
    }

    @Test
    public void uploadImageShouldSaveUserWithNewImage() {
        User testUser = new User(1, "e", "f", "l", "p", Role.USER,
                "i", "ep", null, null);

        when(repository.findUserByEmail(testUser.getEmail())).thenReturn(testUser);
        when(imageService.uploadUserAvatar(any(), any())).thenReturn("str");
        when(repository.save(any())).thenReturn(null);

        userService.uploadImage(testUser.getEmail(), null);

        verify(repository, times(1)).save(any());
    }
}