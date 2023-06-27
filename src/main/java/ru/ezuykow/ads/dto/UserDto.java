package ru.ezuykow.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ezuykow
 */
@Data
@AllArgsConstructor
public class UserDto {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String image;
}
