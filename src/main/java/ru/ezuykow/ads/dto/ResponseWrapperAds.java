package ru.ezuykow.ads.dto;

import lombok.Data;

/**
 * @author ezuykow
 */
@Data
public class ResponseWrapperAds {

    private Integer count;
    private Ads[] results;
}
