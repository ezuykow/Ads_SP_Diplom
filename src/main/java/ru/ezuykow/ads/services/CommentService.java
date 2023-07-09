package ru.ezuykow.ads.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ezuykow.ads.dto.CreateCommentDto;
import ru.ezuykow.ads.dto.FullCommentDto;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.Comment;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.exceptions.NonExistentAdException;
import ru.ezuykow.ads.exceptions.NonExistentCommentException;
import ru.ezuykow.ads.mappers.CommentMapper;
import ru.ezuykow.ads.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author ezuykow
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final UserService userService;
    private final AdService adService;
    private final CommentMapper commentMapper;

    //-----------------API START-----------------

    /**
     * Return all comments of target ad
     * @param adId id of target ad
     * @return {@link List} of {@link Comment}
     * @author ezuykow
     */
    public List<Comment> findAllByAdId(int adId) {
        return repository.findAllByAdPk(adId);
    }

    /**
     * Return comment with target id
     * @param commentId target comment's id
     * @return {@link Optional} with {@link Comment} if target comment exist, <br>
     * {@link Optional#empty()} otherwise
     * @author ezuykow
     */
    public Optional<Comment> findById(int commentId) {
        return repository.findById(commentId);
    }

    /**
     * Create new comment
     * @param username author's username (email)
     * @param createCommentDto object with new comment's data
     * @param adId id of target ad
     * @return created {@link Comment}
     * @author ezuykow
     */
    public Comment createComment(String username, CreateCommentDto createCommentDto, int adId) {
        User author = userService.findUserByEmail(username);
        Ad targetAd = adService.findById(adId).orElseThrow(NonExistentAdException::new);
        return save(new Comment(
                targetAd,
                author,
                System.currentTimeMillis(),
                createCommentDto.getText()));
    }

    /**
     * Edit comment
     * @param commentId id of target comment
     * @param fullCommentDto object with new comment's data
     * @return edited comment in {@link FullCommentDto} instance
     * @author ezuykow
     */
    public FullCommentDto editComment(int commentId, FullCommentDto fullCommentDto) {
        Comment targetComment = findById(commentId).orElseThrow(NonExistentCommentException::new);
        targetComment.setText(fullCommentDto.getText());
        targetComment.setCreatingTime(System.currentTimeMillis());
        return commentMapper.mapCommentToFullCommentDto(save(targetComment));
    }

    /**
     * Save comment
     * @param comment target {@link Comment}
     * @return saved {@link Comment}
     * @author ezuykow
     */
    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    /**
     * Delete target comment
     * @param id target comment's id
     * @author ezuykow
     */
    public void deleteById(int id) {
        repository.deleteById(id);
    }

    //-----------------API END-----------------

}
