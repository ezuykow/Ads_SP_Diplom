package ru.ezuykow.ads.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ezuykow.ads.dto.RegisterReq;
import ru.ezuykow.ads.dto.Role;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder encoder;
    private final UserService userService;
    private final UserDetailService userDetailService;

    //-----------------API START-----------------

    /**
     * Login user by auth data
     * @param userName user's username (email)
     * @param password user's password
     * @return {@code true} if user with this {@code userName} is existed and
     * {@code password} is correct, <br>
     * {@code false} otherwise
     * @author ezuykow
     */
    public boolean login(String userName, String password) {
        UserDetails userDetails = userDetailService.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    /**
     * Register new user
     * @param registerReq object with new user's data
     * @param role role of new user
     * @return {@code true} if new user successfully registered, <br>
     * {@code false} if user with this username is already exist
     * @author ezuykow
     */
    public boolean register(RegisterReq registerReq, Role role) {
        if (userService.findUserByEmail(registerReq.getUsername()) == null) {
            userService.saveUserFromRegReq(registerReq, role, encoder.encode(registerReq.getPassword()));
            return true;
        }
        return false;
    }

    //-----------------API END-----------------
}
