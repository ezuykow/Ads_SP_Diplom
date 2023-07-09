package ru.ezuykow.ads.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ezuykow.ads.dto.CreateCommentDto;
import ru.ezuykow.ads.dto.FullCommentDto;
import ru.ezuykow.ads.entities.Ad;
import ru.ezuykow.ads.entities.Comment;
import ru.ezuykow.ads.entities.User;
import ru.ezuykow.ads.exceptions.NonExistentAdException;
import ru.ezuykow.ads.exceptions.NonExistentCommentException;
import ru.ezuykow.ads.mappers.CommentMapper;
import ru.ezuykow.ads.repositories.CommentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author ezuykow
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private AdService adService;

    @Spy
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void findAllByAdIdShouldCallRepositoryFindAllByAdPkMethod() {
        when(repository.findAllByAdPk(anyInt())).thenReturn(any());

        commentService.findAllByAdId(0);

        verify(repository, only()).findAllByAdPk(anyInt());
    }

    @Test
    public void findByIdShouldCallRepositoryFindByIdMethod() {
        when(repository.findById(any())).thenReturn(any());

        commentService.findById(0);

        verify(repository, only()).findById(any());
    }

    @Test
    public void createCommentShouldThrowNonExistentAdExceptionWhenAdNotExisted() {
        when(userService.findUserByEmail(anyString())).thenReturn(new User());
        when(adService.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NonExistentAdException.class,
                () -> commentService.createComment("email", new CreateCommentDto(), 0));
    }

    @Test
    public void createCommentShouldSaveAndReturnNewComment() {
        CreateCommentDto testCCD = new CreateCommentDto();
        testCCD.setText("text");

        when(userService.findUserByEmail(anyString())).thenReturn(new User());
        when(adService.findById(anyInt())).thenReturn(Optional.of(new Ad()));
        when(repository.save(any())).thenReturn(new Comment());

        Comment result = commentService.createComment("email", testCCD, 0);

        verify(repository, times(1)).save(any());
        assertInstanceOf(Comment.class, result);
    }

    @Test
    public void editCommentShouldThrowNonExistentCommentExceptionWhenCommentNotExisted() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NonExistentCommentException.class,
                () -> commentService.editComment(0, new FullCommentDto()));
    }

    @Test
    public void editCommentShouldSaveEditedCommentAndReturnFullCommentDto() {
        FullCommentDto testFCD = new FullCommentDto();
        testFCD.setText("newText");

        Comment testTargetComment = new Comment(new Ad(), new User(), 1223L, "text");
        Comment testEditedComment = new Comment(new Ad(), new User(), 5354364L, testFCD.getText());
        FullCommentDto testEditedFCD = commentMapper.mapCommentToFullCommentDto(testEditedComment);

        when(repository.findById(any())).thenReturn(Optional.of(testTargetComment));
        when(repository.save(any())).thenReturn(testEditedComment);

        FullCommentDto result = commentService.editComment(0, testFCD);

        verify(repository, times(1)).save(any());
        assertEquals(testEditedFCD, result);
    }

    @Test
    public void saveShouldOnlyCallRepositorySave() {
        when(repository.save(any())).thenReturn(null);

        commentService.save(null);

        verify(repository, only()).save(any());
    }

    @Test
    public void deleteByIdShouldOnlyCallRepositoryDeleteById() {
        doNothing().when(repository).deleteById(any());

        commentService.deleteById(0);

        verify(repository, only()).deleteById(any());
    }
}