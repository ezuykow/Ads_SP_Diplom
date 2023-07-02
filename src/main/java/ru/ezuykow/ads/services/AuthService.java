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

    public boolean login(String userName, String password) {
        UserDetails userDetails = userDetailService.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    public boolean register(RegisterReq registerReq, Role role) {
        if (userService.findUserByEmail(registerReq.getUsername()) == null) {
            userService.saveUserFromRegReq(registerReq, role, encoder.encode(registerReq.getPassword()));
            return true;
        }
        return false;
    }

    //-----------------API END-----------------
}
