package ru.ezuykow.ads.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.ezuykow.ads.dto.RegisterReq;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.entities.User;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;
    private final UserService userService;

    //-----------------API START-----------------

    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            if (!loadUserFromDB(userName)) {
                return false;
            }
        }

        UserDetails userDetails = manager.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    public boolean register(RegisterReq registerReq, Role role) {
        if (manager.userExists(registerReq.getUsername())) {
            return false;
        }
        manager.createUser(
                org.springframework.security.core.userdetails.User.builder()
                        .passwordEncoder(this.encoder::encode)
                        .password(registerReq.getPassword())
                        .username(registerReq.getUsername())
                        .roles(role.name())
                        .build());

        userService.saveUserFromRegReq(registerReq, role, encoder.encode(registerReq.getPassword()));

        return true;
    }

    //-----------------API END-----------------

    private boolean loadUserFromDB(String userName) {
        User targetUser = userService.findUserByEmail(userName);
        if (targetUser == null) {
            return false;
        }

        manager.createUser(
                org.springframework.security.core.userdetails.User.builder()
                        .password(targetUser.getEncodedPassword())
                        .username(targetUser.getEmail())
                        .roles(targetUser.getRole())
                        .build());
        return true;
    }
}
