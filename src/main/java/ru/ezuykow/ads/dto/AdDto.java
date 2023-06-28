package ru.ezuykow.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ezuykow
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdDto {

    private Integer author;
    private String image;
    private Integer pk;
    private Integer price;
    private String title;
}
