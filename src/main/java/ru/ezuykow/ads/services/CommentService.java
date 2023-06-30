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

    public List<Comment> findAllByAdId(int adId) {
        return repository.findAllByAdPk(adId);
    }

    public Optional<Comment> findById(int commentId) {
        return repository.findById(commentId);
    }

    public Comment createComment(String username, CreateCommentDto createCommentDto, int adId) {
        User author = userService.findUserByEmail(username);
        Ad targetAd = adService.findById(adId).orElseThrow(NonExistentAdException::new);
        return save(new Comment(
                0,
                targetAd,
                author,
                System.currentTimeMillis(),
                createCommentDto.getText()));
    }

    public FullCommentDto editComment(int commentId, FullCommentDto fullCommentDto) {
        Comment targetComment = findById(commentId).orElseThrow(NonExistentCommentException::new);
        targetComment.setText(fullCommentDto.getText());
        targetComment.setCreatingTime(System.currentTimeMillis());
        return commentMapper.mapCommentToFullCommentDto(save(targetComment));
    }


    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }
}
