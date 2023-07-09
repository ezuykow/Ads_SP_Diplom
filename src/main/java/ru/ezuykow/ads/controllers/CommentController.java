package ru.ezuykow.ads.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ezuykow.ads.dto.CreateCommentDto;
import ru.ezuykow.ads.dto.FullCommentDto;
import ru.ezuykow.ads.dto.ResponseWrapperComment;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.entities.Comment;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.exceptions.NonExistentCommentException;
import ru.ezuykow.ads.mappers.CommentMapper;
import ru.ezuykow.ads.services.AdService;
import ru.ezuykow.ads.services.CommentService;
import ru.ezuykow.ads.services.UserService;

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
    private final UserService userService;

    //-----------------API START-----------------

    /**
     * Return all comments of target ad
     * @param adId {@code id} of target ad (URL variable)
     * @return {@link HttpStatus#OK} with all comments in {@link ResponseWrapperComment} instance if target ad existed, <br>
     * {@link HttpStatus#NOT_FOUND} otherwise
     * @author ezuykow
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<ResponseWrapperComment> getAllCommentsByAd(@PathVariable("id") int adId) {
        if (isAdExist(adId)) {
            return ResponseEntity.ok(commentMapper.mapCommentListToWrapper(commentService.findAllByAdId(adId)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Return target comment of target ad
     * @param adId {@code id} of target ad
     * @param commentId {@code id} of target comment
     * @return {@link HttpStatus#OK} with comment in {@link FullCommentDto} instance
     * if target ad and target comment are existed, <br>
     * {@link HttpStatus#NOT_FOUND} otherwise
     * @author ezuykow
     */
    @GetMapping("/{id}/comments/{commentId}")
    public ResponseEntity<FullCommentDto> getCommentByAdAndCommentIds(
            @PathVariable("id") int adId,
            @PathVariable("commentId") int commentId)
    {
        Optional<Comment> targetCommentOpt = commentService.findById(commentId);
        if (isAdExist(adId) && targetCommentOpt.isPresent()) {
            return ResponseEntity.ok(commentMapper.mapCommentToFullCommentDto(targetCommentOpt.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Create comment for target ad
     * @param authentication (inject) user auth data
     * @param adId {@code id} of target ad
     * @param createCommentDto new comment data
     * @return {@link HttpStatus#OK} with created comment in {@link FullCommentDto} instance
     * if target ad existed, <br>
     * {@link HttpStatus#NOT_FOUND} otherwise
     * @author ezuykow
     */
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
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Edit target comment
     * @param authentication user auth data
     * @param adId id of target ad
     * @param commentId id of target comment
     * @param fullCommentDto object with new comment's data
     * @return {@link HttpStatus#OK} with edited comment in {@link FullCommentDto}
     * if target ad and comment are existed and initiator is admin or comment's author, <br>
     * {@link HttpStatus#NOT_FOUND} if target ad or comment not existent, <br>
     * {@link HttpStatus#FORBIDDEN} if initiator not admin and not comment's author
     * @author ezuykow
     */
    @PatchMapping("/{id}/comments/{commentId}")
    public ResponseEntity<FullCommentDto> editComment(
            Authentication authentication,
            @PathVariable("id") int adId,
            @PathVariable("commentId") int commentId,
            @RequestBody FullCommentDto fullCommentDto)
    {
        Optional<Comment> targetCommentOpt = commentService.findById(commentId);
        if (isAdExist(adId) && targetCommentOpt.isPresent()) {
            if (isUserAdminOrAuthor(authentication.getName(), targetCommentOpt))  {
                return ResponseEntity.ok(commentService.editComment(commentId, fullCommentDto));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Delete target comment
     * @param authentication user auth data
     * @param adId id of target ad
     * @param commentId id of target comment
     * @return {@link HttpStatus#OK} if target ad and comment are existed and initiator is admin or comment's author, <br>
     * {@link HttpStatus#NOT_FOUND} if target ad or comment not existent, <br>
     * {@link HttpStatus#FORBIDDEN} if initiator not admin and not comment's author
     * @author ezuykow
     */
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            Authentication authentication,
            @PathVariable("id") int adId,
            @PathVariable("commentId") int commentId)
    {
        Optional<Comment> targetCommentOpt = commentService.findById(commentId);
        if (isAdExist(adId) && targetCommentOpt.isPresent()) {
            if (isUserAdminOrAuthor(authentication.getName(), targetCommentOpt)) {
                commentService.deleteById(commentId);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    //-----------------API END-----------------

    /**
     * Check that target ad is existed
     * @param adId id of target ad
     * @return true if ad existed, <br>
     * false otherwise
     * @author ezuykow
     */
    private boolean isAdExist(int adId) {
        return adService.findById(adId).isPresent();
    }

    /**
     * Check that user is admin or comment's author
     * @param targetEmail target user email
     * @param targetCommentOpt target comment (Optional)
     * @return {@code true} if user with {@code targetEmail} is admin or author of {@code targetComment}, <br>
     * {@code false} if not
     * @author ezuykow
     */
    private boolean isUserAdminOrAuthor(String targetEmail, Optional<Comment> targetCommentOpt) {
        User initiator = userService.findUserByEmail(targetEmail);
        return initiator.getRole() == Role.ADMIN
                || initiator.getEmail()
                .equals(targetCommentOpt.orElseThrow(NonExistentCommentException::new).getAuthor().getEmail());
    }
}
