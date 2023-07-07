package ru.ezuykow.ads.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.ezuykow.ads.dto.AdDto;
import ru.ezuykow.ads.dto.CreateAdDto;
import ru.ezuykow.ads.dto.FullAdDto;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.mappers.AdMapper;
import ru.ezuykow.ads.repositories.AdRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author ezuykow
 */
@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository repository;
    private final AdMapper adMapper;
    private final UserService userService;
    private final ImageService imageService;

    //-----------------API START-----------------

    public List<Ad> findAll() {
        return repository.findAll();
    }

    public Optional<Ad> findById(int id) {
        return repository.findById(id);
    }

    @Transactional
    public AdDto createAd(String username, MultipartFile image, CreateAdDto createAdsDto) {
        Ad newAd = adMapper.mapCreateAdDtoToAd(createAdsDto);
        newAd.setAuthor(userService.findUserByEmail(username));
        newAd = save(newAd);

        newAd.setImage(imageService.uploadAdImage(newAd.getPk(), image));
        save(newAd);

        return adMapper.mapEntityToDto(newAd);
    }

    public FullAdDto createFullAd(Ad ad) {
        User author = ad.getAuthor();
        return adMapper.mapAdAndAuthorToFullAdDto(ad, author);
    }

    public AdDto editAd(Ad targetAd, CreateAdDto newData) {
        targetAd.setDescription(newData.getDescription());
        targetAd.setPrice(newData.getPrice());
        targetAd.setTitle(newData.getTitle());
        save(targetAd);
        return adMapper.mapEntityToDto(targetAd);
    }

    @Transactional
    public byte[] editAdImage(Ad ad, MultipartFile imageFile) {
        imageService.uploadAdImage(ad.getPk(), imageFile);
        return imageService.getAdImage(ad.getPk().toString());
    }

    public Ad save(Ad ad) {
        return repository.save(ad);
    }

    public void deleteById(int id) {
        imageService.deleteAdImage(id);
        repository.deleteById(id);
    }

    //-----------------API END-----------------
}
