package ru.ezuykow.ads.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ezuykow.ads.dto.NewPassword;
import ru.ezuykow.ads.dto.UserDto;
import ru.ezuykow.ads.services.UserService;

/**
 * @author ezuykow
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //-----------------API START-----------------

    /**
     * Return user's data
     * @param authentication user auth data
     * @return {@link HttpStatus#OK} with user's data in {@link UserDto} instance
     * @author ezuykow
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(Authentication authentication) {
        return ResponseEntity.ok(userService.findUserDtoByEmail(authentication.getName()));
    }

    /**
     * Set password of target user
     * @param authentication user auth data
     * @param newPasswordDto object with new and current passwords
     * @return {@link HttpStatus#OK} if password successfully set, <br>
     * {@link HttpStatus#BAD_REQUEST} otherwise
     * @author ezuykow
     */
    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(Authentication authentication,
                                         @RequestBody NewPassword newPasswordDto) {
        if (userService.setPassword(authentication.getName(), newPasswordDto)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Edit user's data
     * @param authentication user auth data
     * @param userDto object with new user's data
     * @return {@link HttpStatus#OK} with new user's data in {@link UserDto} instance
     * @author ezuykow
     */
    @PatchMapping("/me")
    public ResponseEntity<UserDto> editUser(Authentication authentication,
                                      @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.editUser(authentication.getName(), userDto));
    }

    /**
     * Edit user's avatar
     * @param authentication user auth data
     * @param image {@link MultipartFile} with new avatar
     * @return {@link HttpStatus#OK} when user's avatar edited
     * @author ezuykow
     */
    @PatchMapping(value = "me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editImage(Authentication authentication,
                                       @RequestPart("image") MultipartFile image) {
        userService.uploadImage(authentication.getName(), image);
        return ResponseEntity.ok().build();
    }

    //-----------------API END-----------------

}
