package ru.ezuykow.ads.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ezuykow.ads.dto.LoginReq;
import ru.ezuykow.ads.dto.RegisterReq;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.services.AuthService;

import static ru.ezuykow.ads.dto.Role.USER;

/**
 * @author ezuykow
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //-----------------API START-----------------

    /**
     * Authorize user
     * @param req {@link LoginReq} with auth data
     * @return {@link HttpStatus#OK} if auth successful, <br>
     * {@link HttpStatus#UNAUTHORIZED} otherwise
     * @author ezuykow
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        if (authService.login(req.getUsername(), req.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Register new user
     * @param req {@link RegisterReq} with registration data
     * @return {@link HttpStatus#CREATED} if registration successful, <br>
     * {@link HttpStatus#BAD_REQUEST} otherwise
     * @author ezuykow
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        Role role = req.getRole() == null ? USER : req.getRole();
        if (authService.register(req, role)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //-----------------API END-----------------

}
