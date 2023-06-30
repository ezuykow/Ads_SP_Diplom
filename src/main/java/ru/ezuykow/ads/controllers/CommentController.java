package ru.ezuykow.ads.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ezuykow.ads.dto.CreateCommentDto;
import ru.ezuykow.ads.dto.FullCommentDto;
import ru.ezuykow.ads.dto.ResponseWrapperComment;
import ru.ezuykow.ads.entities.Comment;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.mappers.CommentMapper;
import ru.ezuykow.ads.services.AdService;
import ru.ezuykow.ads.services.CommentService;

import java.util.Optional;

/**
 * @author ezuykow
 */
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final AdService adService;

    @GetMapping("/{id}/comments")
    public ResponseEntity<ResponseWrapperComment> getAllCommentsByAd(@PathVariable("id") int adId) {
        if (isAdExist(adId)) {
            return ResponseEntity.ok(commentMapper.mapCommentListToWrapper(commentService.findAllByAdId(adId)));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<FullCommentDto> addCommentToAd(
            Authentication authentication,
            @PathVariable("id") int adId,
            @RequestBody CreateCommentDto createCommentDto)
    {
        if (isAdExist(adId)) {
            return ResponseEntity.ok(commentMapper.mapCommentToFullCommentDto(
                    commentService.createComment(authentication.getName(), createCommentDto, adId)));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/comments/{commentId}")
    public ResponseEntity<FullCommentDto> editComment(
            Authentication authentication,
            @PathVariable("id") int adId,
            @PathVariable("commentId") int commentId,
            @RequestBody FullCommentDto fullCommentDto)
    {
        Optional<Comment> targetCommentOpt = commentService.findById(commentId);
        if (isAdExist(adId) && targetCommentOpt.isPresent()) {
            User targetCommentAuthor = targetCommentOpt.get().getAuthor();
            if (targetCommentAuthor.getEmail().equals(authentication.getName())) {
                return ResponseEntity.ok(commentService.editComment(commentId, fullCommentDto));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            Authentication authentication,
            @PathVariable("id") int adId,
            @PathVariable("commentId") int commentId)
    {
        Optional<Comment> targetCommentOpt = commentService.findById(commentId);
        if (isAdExist(adId) && targetCommentOpt.isPresent()) {
            User targetCommentAuthor = targetCommentOpt.get().getAuthor();
            if (targetCommentAuthor.getEmail().equals(authentication.getName())) {
                commentService.deleteById(commentId);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private boolean isAdExist(int adId) {
        return adService.findById(adId).isPresent();
    }
}
