package ru.ezuykow.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ezuykow
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapperComment {

    private Integer count;
    private List<FullCommentDto> results;
}
