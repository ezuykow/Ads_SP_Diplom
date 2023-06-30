package ru.ezuykow.ads.mappers;

import org.springframework.stereotype.Component;
import ru.ezuykow.ads.dto.AdDto;
import ru.ezuykow.ads.dto.CreateAdDto;
import ru.ezuykow.ads.dto.FullAdDto;
import ru.ezuykow.ads.dto.ResponseWrapperAds;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ezuykow
 */
@Component
public class AdMapper {

    public ResponseWrapperAds mapAdsListToResponseWrapperAds(List<Ad> ads) {
        List<AdDto> dtoList = ads.stream().map(this::mapEntityToDto).collect(Collectors.toList());

        return new ResponseWrapperAds(
                dtoList.size(),
                dtoList.toArray(new AdDto[0])
        );
    }

    public Ad mapCreateAdDtoToAd(CreateAdDto dto) {
        return new Ad(
                0,
                0,
                "",
                dto.getPrice(),
                dto.getTitle(),
                dto.getDescription()
        );
    }

    public AdDto mapEntityToDto(Ad ad) {
        return new AdDto(
                ad.getAuthor(),
                ad.getImage(),
                ad.getPk(),
                ad.getPrice(),
                ad.getTitle()
        );
    }

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
}