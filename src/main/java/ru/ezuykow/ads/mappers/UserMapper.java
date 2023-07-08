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

    /**
     * Map {@link User} to {@link UserDto}
     * @param user target {@link User}
     * @return created {@link UserDto}
     * @author ezuykow
     */
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

    /**
     * Create new {@link User} from register request
     * @param regReq register request
     * @param role user's role
     * @param encodedPassword user's encoded password
     * @return created {@link User}
     * @author ezuykow
     */
    public User mapRegReqToUser(RegisterReq regReq, Role role, String encodedPassword) {
        return new User(
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
