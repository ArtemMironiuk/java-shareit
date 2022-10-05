package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "items", schema = "public")
public class Item {                //вещь

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;               //уникальный идентификатор вещи
    @NonNull
    @NotBlank
    @Column(name = "item_name", nullable = false)
    private String name;           //краткое название
    @NonNull
    @NotBlank
    @Column(name = "description", nullable = false)
    private String description;    //развернутое описание
    @NonNull
    @Column(name = "available", nullable = false)
    private Boolean available;     //статус о том, доступна или нет вещь для аренды
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;           //владелец вещи
//    @Column(name = "request_id")
//    private Long requestId;          //если вещь была создана по запросу другого пользователя, то в этом поле будет ссылка на запрос
}
