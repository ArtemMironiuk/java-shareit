package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCommentDto {

    private Long id;
    private String name;
    private Boolean available;
    private String description;
    private List<CommentDto> listComments;
}
