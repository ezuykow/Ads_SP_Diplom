package ru.ezuykow.ads.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.ezuykow.ads.dto.NewPassword;
import ru.ezuykow.ads.dto.RegisterReq;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.dto.UserDto;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.mappers.UserMapper;
import ru.ezuykow.ads.repositories.UserRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ezuykow
 */
@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${avatars.dir.path}")
    private String avatarsPath;

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    //-----------------API START-----------------

    public boolean setPassword(String email, NewPassword newPasswordDto) {
        User targetUser = findUserByEmail(email);
        if (encoder.matches(newPasswordDto.getCurrentPassword(), targetUser.getEncodedPassword())) {
            targetUser.setEncodedPassword(encoder.encode(newPasswordDto.getNewPassword()));
            save(targetUser);
            return true;
        }
        return false;
    }

    public UserDto findUserDtoByEmail(String email) {
        return userMapper.mapEntityToDto(findUserByEmail(email));
    }

    public User findUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }

    public void save(User user) {
        repository.save(user);
    }

    public void saveUserFromRegReq(RegisterReq registerReq, Role role, String encodedPassword) {
        repository.save(userMapper.mapRegReqToUser(registerReq, role, encodedPassword));
    }

    public User editUser(String targetEmail, UserDto dto) {
        User targetUser = findUserByEmail(targetEmail);
        targetUser.setFirstName(dto.getFirstName());
        targetUser.setLastName(dto.getLastName());
        targetUser.setPhone(dto.getPhone());
        save(targetUser);
        return targetUser;
    }

    @Transactional
    public void uploadImage(String targetEmail, MultipartFile image) {
        User targetUser = findUserByEmail(targetEmail);

        Path filePath = Path.of(avatarsPath, targetEmail + ".png");

        try {
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);
            image.transferTo(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        targetUser.setImage("/" + avatarsPath + "/" + targetEmail);
        save(targetUser);
    }

    //-----------------API END-----------------

}
