package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
    @Column(name = "id")
    private Long id;                //уникальный идентификатор
    @NonNull
    @NotBlank
    @Column(name = "description", nullable = false)
    private String description;     //текст запроса, содержащий описание требуемой вещи
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requestor;         //пользователь, создавший запрос
    @Column(name = "created", nullable = false)
    private LocalDateTime created;  //дата и время создания запроса
}
