package ru.ezuykow.ads.mappers;

import org.springframework.stereotype.Component;
import ru.ezuykow.ads.dto.AdDto;
import ru.ezuykow.ads.dto.CreateAdDto;
import ru.ezuykow.ads.dto.FullAdDto;
import ru.ezuykow.ads.dto.ResponseWrapperAds;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ezuykow
 */
@Component
public class AdMapper {

    //-----------------API START-----------------

    /**
     * Map {@link List} of {@link Ad} to {@link ResponseWrapperAds}
     * @param ads {@link List} of {@link Ad}
     * @return created {@link ResponseWrapperAds}
     * @author ezuykow
     */
    public ResponseWrapperAds mapAdsListToResponseWrapperAds(List<Ad> ads) {
        List<AdDto> dtoList = ads.stream().map(this::mapEntityToDto).collect(Collectors.toList());
        return new ResponseWrapperAds(dtoList.size(), dtoList);
    }

    /**
     * Map {@link CreateAdDto} to {@link Ad}
     * @param dto target {@link CreateAdDto}
     * @return created {@link Ad}
     * @author ezuykow
     */
    public Ad mapCreateAdDtoToAd(CreateAdDto dto) {
        return new Ad(
                new User(),
                "",
                dto.getPrice(),
                dto.getTitle(),
                dto.getDescription(),
                new ArrayList<>()
        );
    }

    /**
     * Map {@link Ad} to {@link AdDto}
     * @param ad target {@link Ad}
     * @return created {@link AdDto}
     * @author ezuykow
     */
    public AdDto mapEntityToDto(Ad ad) {
        return new AdDto(
                ad.getAuthor().getUserId(),
                ad.getImage(),
                ad.getPk(),
                ad.getPrice(),
                ad.getTitle()
        );
    }

    /**
     * Map {@link Ad} and {@link User} to {@link FullAdDto}
     * @param ad target {@link Ad}
     * @param author target {@link User}
     * @return created {@link FullAdDto}
     * @author ezuykow
     */
    public FullAdDto mapAdAndAuthorToFullAdDto(Ad ad, User author) {
        return new FullAdDto(
                ad.getPk(),
                author.getFirstName(),
                author.getLastName(),
                ad.getDescription(),
                author.getEmail(),
                ad.getImage(),
                author.getPhone(),
                ad.getPrice(),
                ad.getTitle()
        );
    }

    //-----------------API END-----------------

}
