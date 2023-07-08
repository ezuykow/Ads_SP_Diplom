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

    /**
     * Return all ads
     * @return {@link List<Ad>} of {@link Ad}
     * @author ezuykow
     */
    public List<Ad> findAll() {
        return repository.findAll();
    }

    /**
     * Return target ad
     * @param id id of target ad
     * @return {@link Optional<Ad>} with target {@link Ad}, <br>
     * {@link Optional#empty()} if target ad not existed
     * @author ezuykow
     */
    public Optional<Ad> findById(int id) {
        return repository.findById(id);
    }

    /**
     * Create new ad
     * @param username author's username (email)
     * @param image new ad's image
     * @param createAdsDto new ad's data
     * @return created ad in {@link AdDto} instance
     * @author ezuykow
     */
    @Transactional
    public AdDto createAd(String username, MultipartFile image, CreateAdDto createAdsDto) {
        Ad newAd = adMapper.mapCreateAdDtoToAd(createAdsDto);
        newAd.setAuthor(userService.findUserByEmail(username));
        newAd = save(newAd);

        newAd.setImage(imageService.uploadAdImage(newAd.getPk(), image));
        save(newAd);

        return adMapper.mapEntityToDto(newAd);
    }

    /**
     * Create {@link FullAdDto} instance
     * @param ad target {@link Ad}
     * @return {@link FullAdDto} instance for target {@link Ad}
     * @author ezuykow
     */
    public FullAdDto createFullAd(Ad ad) {
        User author = ad.getAuthor();
        return adMapper.mapAdAndAuthorToFullAdDto(ad, author);
    }

    /**
     * Edit target ad
     * @param targetAd target {@link Ad}
     * @param newData {@link CreateAdDto} with new ad's data
     * @return edited ad in {@link AdDto} instance
     * @author ezuykow
     */
    public AdDto editAd(Ad targetAd, CreateAdDto newData) {
        targetAd.setDescription(newData.getDescription());
        targetAd.setPrice(newData.getPrice());
        targetAd.setTitle(newData.getTitle());
        save(targetAd);
        return adMapper.mapEntityToDto(targetAd);
    }

    /**
     * Edit ad's image
     * @param ad target {@link Ad}
     * @param imageFile {@link MultipartFile} with new image
     * @return {@code byte[]} - bytes of new image
     * @author ezuykow
     */
    @Transactional
    public byte[] editAdImage(Ad ad, MultipartFile imageFile) {
        imageService.uploadAdImage(ad.getPk(), imageFile);
        return imageService.getAdImage(ad.getPk().toString());
    }

    /**
     * Save ad
     * @param ad target {@link Ad}
     * @return saved {@link Ad}
     * @author ezuykow
     */
    public Ad save(Ad ad) {
        return repository.save(ad);
    }

    /**
     * Delete target ad and it's image
     * @param id id of target ad
     * @author ezuykow
     */
    public void deleteById(int id) {
        imageService.deleteAdImage(id);
        repository.deleteById(id);
    }

    //-----------------API END-----------------
}
