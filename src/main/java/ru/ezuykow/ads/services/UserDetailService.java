package ru.ezuykow.ads.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ezuykow.ads.entities.User;

/**
 * @author ezuykow
 */
@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserService userService;

    //-----------------API START-----------------

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User targetUser = userService.findUserByEmail(username);
        if (targetUser != null) {
            return buildUserDetails(targetUser);
        }
        throw new UsernameNotFoundException("Unknown user " + username);
    }

    //-----------------API END-----------------

    private UserDetails buildUserDetails(User targetUser) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(targetUser.getEmail())
                .password(targetUser.getEncodedPassword())
                .roles(targetUser.getRole().name())
                .build();
    }
}
