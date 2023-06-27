package ru.ezuykow.ads.dto;

import lombok.Data;

/**
 * @author ezuykow
 */
@Data
public class FullAds {

    private Integer pk;
    private String authorFirstName;
    private String authorLastName;
    private String description;
    private String email;
    private String image;
    private String phone;
    private Integer price;
    private String title;
}
