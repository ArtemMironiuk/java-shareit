package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text")
    private String text;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
