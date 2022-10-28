package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "items_requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                //уникальный идентификатор
    @Column(name = "description", nullable = false)
    private String description;     //текст запроса, содержащий описание требуемой вещи
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;         //пользователь, создавший запрос
    @Column(name = "created", nullable = false)
    private LocalDateTime created;  //дата и время создания запроса
}
