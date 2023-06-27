package ru.ezuykow.ads.dto;

import lombok.Data;

/**
 * @author ezuykow
 */
@Data
public class ResponseWrapperComment {

    private Integer count;
    private Comment[] results;
}
