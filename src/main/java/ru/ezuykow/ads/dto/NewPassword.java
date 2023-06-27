package ru.ezuykow.ads.dto;

import lombok.Data;

/**
 * @author ezuykow
 */
@Data
public class NewPassword {

    private String currentPassword;
    private String newPassword;
}
