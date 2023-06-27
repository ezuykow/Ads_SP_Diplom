package ru.ezuykow.ads.mappers;

import org.springframework.stereotype.Component;
import ru.ezuykow.ads.dto.RegisterReq;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.dto.UserDto;
import ru.ezuykow.ads.entities.User;

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

    public User mapDtoToEntity(UserDto dto) {
        return new User(
                dto.getId(),
                dto.getEmail(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPhone(),
                null,
                dto.getImage(),
                null
        );
    }

    public User mapRegReqToUser(RegisterReq regReq, Role role, String encodedPassword) {
        return new User(
                0,
                regReq.getUsername(),
                regReq.getFirstName(),
                regReq.getLastName(),
                regReq.getPhone(),
                role.name(),
                null,
                encodedPassword
        );
    }

    //-----------------API END-----------------

}
