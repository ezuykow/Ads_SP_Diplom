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
public class CreateAdDto {

    private String description;
    private Integer price;
    private String title;
}
