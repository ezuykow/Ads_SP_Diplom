package ru.ezuykow.ads.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.ezuykow.ads.dto.FullCommentDto;
import ru.ezuykow.ads.dto.ResponseWrapperComment;
import ru.ezuykow.ads.dto.Role;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.Comment;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.mappers.CommentMapper;
import ru.ezuykow.ads.services.AdService;
import ru.ezuykow.ads.services.CommentService;
import ru.ezuykow.ads.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;
    @Mock
    private AdService adService;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;

    @Spy
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentController commentController;

    @Test
    public void getAllCommentsByIdShouldReturnNotFoundIfAdNotExisted() {
        when(adService.findById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<ResponseWrapperComment> response = commentController.getAllCommentsByAd(0);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAllCommentsByIdShouldReturnResponseWrapperComment() {
        Ad testAd = new Ad(10, new User(), "im", 123, "tit", "desc",
                null);
        Comment testComment = new Comment(testAd, new User(), 34324234L, "text");
        List<Comment> testCommentsList = List.of(testComment);
        testAd.setComments(testCommentsList);

        when(adService.findById(testAd.getPk())).thenReturn(Optional.of(testAd));
        when(commentService.findAllByAdId(testAd.getPk())).thenReturn(testCommentsList);

        ResponseEntity<ResponseWrapperComment> response = commentController.getAllCommentsByAd(testAd.getPk());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCommentsList.size(), response.getBody().getCount());
    }

    @Test
    public void getCommentByAdAndCommentIdsShouldReturnNotFoundIfAdNotExistent() {
        when(adService.findById(anyInt())).thenReturn(Optional.empty());
        when(commentService.findById(anyInt())).thenReturn(Optional.of(new Comment()));

        ResponseEntity<FullCommentDto> response = commentController.getCommentByAdAndCommentIds(0, 0);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getCommentByAdAndCommentIdsShouldReturnNotFoundIfCommentNotExistent() {
        Ad testAd = new Ad(10, new User(), "im", 123, "tit", "desc",
                null);

        when(adService.findById(testAd.getPk())).thenReturn(Optional.of(testAd));
        when(commentService.findById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<FullCommentDto> response = commentController.getCommentByAdAndCommentIds(testAd.getPk(), 0);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getCommentByAdAndCommentIdsShouldReturnStatusOkAndFullCommentDto() {
        Ad testAd = new Ad(10, new User(), "im", 123, "tit", "desc",
                null);
        Comment testComment = new Comment(testAd, new User(), 34324234L, "text");
        testComment.setId(25);
        testAd.setComments(List.of(testComment));

        when(adService.findById(testAd.getPk())).thenReturn(Optional.of(testAd));
        when(commentService.findById(testComment.getId())).thenReturn(Optional.of(testComment));

        ResponseEntity<FullCommentDto> response =
                commentController.getCommentByAdAndCommentIds(testAd.getPk(), testComment.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(FullCommentDto.class, response.getBody());
    }

    @Test
    public void addCommentToAdShouldReturnNotFoundWhenAdNotExisted() {
        when(adService.findById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<FullCommentDto> response =
                commentController.addCommentToAd(authentication, 0, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addCommentToAdShouldReturnStatusOkAndCreatedFullCommentDto() {
        Ad testAd = new Ad(10, new User(), "im", 123, "tit", "desc",
                null);
        Comment testComment = new Comment(testAd, new User(), 34324234L, "text");


        when(adService.findById(testAd.getPk())).thenReturn(Optional.of(testAd));
        when(authentication.getName()).thenReturn("");
        when(commentService.createComment(any(), any(), anyInt())).thenReturn(testComment);

        ResponseEntity<FullCommentDto> response =
                commentController.addCommentToAd(authentication, testAd.getPk(), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(FullCommentDto.class, response.getBody());
    }

    @Test
    public void editCommentShouldReturnStatusNotFoundThenAdNotExisted() {
        when(adService.findById(anyInt())).thenReturn(Optional.empty());
        when(commentService.findById(anyInt())).thenReturn(Optional.of(new Comment()));

        ResponseEntity<FullCommentDto> response =
                commentController.editComment(authentication, 0, 0, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void editCommentShouldReturnStatusNotFoundThenCommentNotExisted() {
        when(adService.findById(anyInt())).thenReturn(Optional.of(new Ad()));
        when(commentService.findById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<FullCommentDto> response =
                commentController.editComment(authentication, 0, 0, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void editCommentShouldReturnForbiddenIfInitiatorNotAdminNotAuthor() {
        Ad testAd = new Ad(10, new User(), "im", 123, "tit", "desc",
                null);
        Comment testComment = new Comment(testAd, new User(), 34324234L, "text");
        testComment.setId(25);
        List<Comment> comments = List.of(testComment);
        testAd.setComments(comments);
        User author = new User("email", "fn", "ln", "p", Role.USER, "i",
                "p", List.of(testAd), comments);
        testAd.setAuthor(author);
        testComment.setAuthor(author);

        String notAuthorEmail = "not author username";
        User notAuthor = new User();
        notAuthor.setEmail(notAuthorEmail);

        when(commentService.findById(testComment.getId())).thenReturn(Optional.of(testComment));
        when(adService.findById(testAd.getPk())).thenReturn(Optional.of(testAd));
        when(authentication.getName()).thenReturn(notAuthorEmail);
        when(userService.findUserByEmail(notAuthorEmail)).thenReturn(notAuthor);

        ResponseEntity<FullCommentDto> response =
                commentController.editComment(authentication, testAd.getPk(), testComment.getId(), null);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void editCommentShouldReturnStatusOkAndEditedFullCommentDto() {
        Ad testAd = new Ad(10, new User(), "im", 123, "tit", "desc",
                null);
        Comment testComment = new Comment(testAd, new User(), 34324234L, "text");
        testComment.setId(25);
        List<Comment> comments = List.of(testComment);
        testAd.setComments(comments);
        User author = new User("email", "fn", "ln", "p", Role.USER, "i",
                "p", List.of(testAd), comments);
        testAd.setAuthor(author);
        testComment.setAuthor(author);

        when(commentService.findById(testComment.getId())).thenReturn(Optional.of(testComment));
        when(adService.findById(testAd.getPk())).thenReturn(Optional.of(testAd));
        when(authentication.getName()).thenReturn(author.getEmail());
        when(userService.findUserByEmail(author.getEmail())).thenReturn(author);
        when(commentService.editComment(anyInt(), any())).thenReturn(new FullCommentDto());

        ResponseEntity<FullCommentDto> response =
                commentController.editComment(authentication, testAd.getPk(), testComment.getId(), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(FullCommentDto.class, response.getBody());
    }

    @Test
    public void deleteCommentShouldReturnStatusNotFoundThenAdNotExisted() {
        when(adService.findById(anyInt())).thenReturn(Optional.empty());
        when(commentService.findById(anyInt())).thenReturn(Optional.of(new Comment()));

        ResponseEntity<?> response =
                commentController.deleteComment(authentication, 0, 0);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteCommentShouldReturnStatusNotFoundThenCommentNotExisted() {
        when(adService.findById(anyInt())).thenReturn(Optional.of(new Ad()));
        when(commentService.findById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<?> response =
                commentController.deleteComment(authentication, 0, 0);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteCommentShouldReturnForbiddenIfInitiatorNotAdminNotAuthor() {
        Ad testAd = new Ad(10, new User(), "im", 123, "tit", "desc",
                null);
        Comment testComment = new Comment(testAd, new User(), 34324234L, "text");
        testComment.setId(25);
        List<Comment> comments = List.of(testComment);
        testAd.setComments(comments);
        User author = new User("email", "fn", "ln", "p", Role.USER, "i",
                "p", List.of(testAd), comments);
        testAd.setAuthor(author);
        testComment.setAuthor(author);

        String notAuthorEmail = "not author username";
        User notAuthor = new User();
        notAuthor.setEmail(notAuthorEmail);

        when(commentService.findById(testComment.getId())).thenReturn(Optional.of(testComment));
        when(adService.findById(testAd.getPk())).thenReturn(Optional.of(testAd));
        when(authentication.getName()).thenReturn(notAuthorEmail);
        when(userService.findUserByEmail(notAuthorEmail)).thenReturn(notAuthor);

        ResponseEntity<?> response =
                commentController.deleteComment(authentication, testAd.getPk(), testComment.getId());

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void deleteCommentShouldReturnStatusOkAndCallServiceDeleteById() {
        Ad testAd = new Ad(10, new User(), "im", 123, "tit", "desc",
                null);
        Comment testComment = new Comment(testAd, new User(), 34324234L, "text");
        testComment.setId(25);
        List<Comment> comments = List.of(testComment);
        testAd.setComments(comments);
        User author = new User("email", "fn", "ln", "p", Role.USER, "i",
                "p", List.of(testAd), comments);
        testAd.setAuthor(author);
        testComment.setAuthor(author);

        when(commentService.findById(testComment.getId())).thenReturn(Optional.of(testComment));
        when(adService.findById(testAd.getPk())).thenReturn(Optional.of(testAd));
        when(authentication.getName()).thenReturn(author.getEmail());
        when(userService.findUserByEmail(author.getEmail())).thenReturn(author);
        doNothing().when(commentService).deleteById(testComment.getId());

        ResponseEntity<?> response =
                commentController.deleteComment(authentication, testAd.getPk(), testComment.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService, times(1)).deleteById(testComment.getId());
    }
}