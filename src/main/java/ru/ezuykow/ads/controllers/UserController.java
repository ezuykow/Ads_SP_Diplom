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

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok().body(userService.findUserDtoByEmail(authentication.getName()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(Authentication authentication,
                                         @RequestBody NewPassword newPasswordDto) {
        if (authentication.isAuthenticated()) {
            userService.setPassword(authentication.getName(), newPasswordDto);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PatchMapping("/me")
    public ResponseEntity<?> editUser(Authentication authentication,
                                      @RequestBody UserDto userDto) {
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok().body(userService.editUser(authentication.getName(), userDto));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PatchMapping(value = "me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editImage(Authentication authentication,
                                       @RequestPart("image") MultipartFile image) {
        if (authentication.isAuthenticated()) {
            userService.uploadImage(authentication.getName(), image);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
