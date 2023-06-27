package ru.ezuykow.ads.dto;

import lombok.Data;

/**
 * @author ezuykow
 */
@Data
public class CreateAds {

    private String description;
    private Integer price;
    private String title;
}
