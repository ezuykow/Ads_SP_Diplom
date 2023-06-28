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
public class ResponseWrapperAds {

    private Integer count;
    private AdDto[] results;
}
