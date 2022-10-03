package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment, String authorName) {
        return new CommentDto(comment.getId(), comment.getText(), authorName, comment.getCreated());
    }
}
