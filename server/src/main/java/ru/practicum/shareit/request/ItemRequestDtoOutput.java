package ru.practicum.shareit.request;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDtoOutput {
    private Long id;
    private String description;
    private LocalDateTime created;
}
