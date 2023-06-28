package ru.ezuykow.ads.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * @author ezuykow
 */
@Service
@RequiredArgsConstructor
public class AdService {

    @Value("${ads.images.dir.path}")
    private String adsImagesDirPath;

    private final AdRepository repository;
    private final AdMapper adMapper;
    private final UserService userService;
    private final CommentService commentService;

    //-----------------API START-----------------

    public List<Ad> findAll() {
        return repository.findAll();
    }

    public List<Ad> findAllByAuthorId(int authorId) {
        return repository.findAllByAuthor(authorId);
    }

    public Optional<Ad> findById(int id) {
        return repository.findById(id);
    }

    @Transactional
    public AdDto createAd(String username, MultipartFile image, CreateAdDto createAdsDto) {
        Ad newAd = adMapper.mapCreateAdDtoToAd(createAdsDto);

        newAd = save(newAd);
        int newAdId = newAd.getPk();

        newAd.setImage(uploadImage(newAdId, image));
        newAd.setAuthor(userService.findUserByEmail(username).getUserId());
        save(newAd);

        return adMapper.mapEntityToDto(newAd);
    }

    public FullAdDto createFullAd(Ad ad) {
        User author = userService.findById(ad.getAuthor());
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
        String filePath = uploadImage(ad.getPk(), imageFile);
        try {
            return Files.readAllBytes(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Ad save(Ad ad) {
        return repository.save(ad);
    }

    public void deleteById(int id) {
        commentService.deleteAllByAdId(id);
        repository.deleteById(id);
    }

    //-----------------API END-----------------

    private String uploadImage(int newAdId, MultipartFile image) {
        Path filePath = Path.of(adsImagesDirPath, newAdId + ".png");

        try {
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);
            image.transferTo(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filePath.toString();
    }
}
