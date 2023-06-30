package ru.ezuykow.ads.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ezuykow.ads.dto.FullCommentDto;
import ru.ezuykow.ads.dto.ResponseWrapperComment;
import ru.ezuykow.ads.entities.Comment;
import ru.ezuykow.ads.entities.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ezuykow
 */
@Component
@RequiredArgsConstructor
public class CommentMapper {

    public ResponseWrapperComment mapCommentListToWrapper(List<Comment> comments) {
        List<FullCommentDto> collect = comments.stream().map(this::mapCommentToFullCommentDto)
                .collect(Collectors.toList());
        return new ResponseWrapperComment(collect.size(), collect.toArray(new FullCommentDto[0]));
    }

    public FullCommentDto mapCommentToFullCommentDto(Comment comment) {
        User author = comment.getAuthor();
        return new FullCommentDto(
                author.getUserId(),
                author.getImage(),
                author.getFirstName(),
                comment.getCreatingTime(),
                comment.getId(),
                comment.getText()
        );
    }
}
