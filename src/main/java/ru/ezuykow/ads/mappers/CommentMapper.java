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

    //-----------------API START-----------------

    /**
     * Map {@link List} of {@link Comment} to {@link ResponseWrapperComment}
     * @param comments target {@link List} of {@link Comment}
     * @return created {@link ResponseWrapperComment}
     * @author ezuykow
     */
    public ResponseWrapperComment mapCommentListToWrapper(List<Comment> comments) {
        List<FullCommentDto> collect = comments.stream().map(this::mapCommentToFullCommentDto)
                .collect(Collectors.toList());
        return new ResponseWrapperComment(collect.size(), collect);
    }

    /**
     * Map {@link Comment} to {@link FullCommentDto}
     * @param comment target {@link Comment}
     * @return created {@link FullCommentDto}
     * @author ezuykow
     */
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

    //-----------------API END-----------------

}
