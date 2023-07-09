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
public class FullCommentDto {

    private Integer author;
    private String authorImage;
    private String authorFirstName;
    private Long createdAt;
    private Integer pk;
    private String text;
}
