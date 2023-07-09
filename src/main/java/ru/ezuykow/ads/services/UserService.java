package ru.ezuykow.ads.services;

import lombok.RequiredArgsConstructor;
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

/**
 * @author ezuykow
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final ImageService imageService;

    //-----------------API START-----------------

    /**
     * Set user's password
     * @param email target user's email (username)
     * @param newPasswordDto object with new and current passwords
     * @return {@code true} if password successfully changed, <br>
     * {@code false} if current password is incorrect
     * @author ezuykow
     */
    public boolean setPassword(String email, NewPassword newPasswordDto) {
        User targetUser = findUserByEmail(email);
        if (encoder.matches(newPasswordDto.getCurrentPassword(), targetUser.getEncodedPassword())) {
            targetUser.setEncodedPassword(encoder.encode(newPasswordDto.getNewPassword()));
            save(targetUser);
            return true;
        }
        return false;
    }

    /**
     * Return {@link UserDto} of target user
     * @param email target user's email (username)
     * @return {@link UserDto} of target user
     * @author ezuykow
     */
    public UserDto findUserDtoByEmail(String email) {
        return userMapper.mapEntityToDto(findUserByEmail(email));
    }

    /**
     * Return {@link User} with target email
     * @param email target user's email (username)
     * @return {@link User} with target email
     * @author ezuykow
     */
    public User findUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }

    /**
     * Save user
     * @param user target {@link User}
     * @author ezuykow
     */
    public void save(User user) {
        repository.save(user);
    }

    /**
     * Save user from {@link RegisterReq} (register request)
     * @param registerReq register request with new user's data
     * @param role role of new user
     * @param encodedPassword new user's encoded password
     * @author ezuykow
     */
    public void saveUserFromRegReq(RegisterReq registerReq, Role role, String encodedPassword) {
        repository.save(userMapper.mapRegReqToUser(registerReq, role, encodedPassword));
    }

    /**
     * Edit user
     * @param targetEmail target user's email (username)
     * @param dto {@link UserDto} with user's new data
     * @return edited user in {@link UserDto} instance
     * @author ezuykow
     */
    public UserDto editUser(String targetEmail, UserDto dto) {
        User targetUser = findUserByEmail(targetEmail);
        targetUser.setFirstName(dto.getFirstName());
        targetUser.setLastName(dto.getLastName());
        targetUser.setPhone(dto.getPhone());
        save(targetUser);
        return userMapper.mapEntityToDto(targetUser);
    }

    /**
     * Upload user's avatar
     * @param targetEmail target user's email (username)
     * @param image {@link MultipartFile} with avatar
     * @author ezuykow
     */
    @Transactional
    public void uploadImage(String targetEmail, MultipartFile image) {
        User targetUser = findUserByEmail(targetEmail);
        targetUser.setImage(imageService.uploadUserAvatar(targetEmail, image));
        save(targetUser);
    }

    //-----------------API END-----------------

}
