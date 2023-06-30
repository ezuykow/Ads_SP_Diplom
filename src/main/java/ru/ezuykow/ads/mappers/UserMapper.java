package ru.ezuykow.ads.mappers;

import org.springframework.stereotype.Component;
import ru.ezuykow.ads.dto.RegisterReq;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.dto.UserDto;
import ru.ezuykow.ads.entities.User;

import java.util.ArrayList;

/**
 * @author ezuykow
 */
@Component
public class UserMapper {


    //-----------------API START-----------------

    public UserDto mapEntityToDto(User user) {
        return new UserDto(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getImage()
        );
    }

    public User mapRegReqToUser(RegisterReq regReq, Role role, String encodedPassword) {
        return new User(
                0,
                regReq.getUsername(),
                regReq.getFirstName(),
                regReq.getLastName(),
                regReq.getPhone(),
                role,
                null,
                encodedPassword,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    //-----------------API END-----------------

}
