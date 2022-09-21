package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {                //вещь
    @Positive
    private Long id;               //уникальный идентификатор вещи
    @NonNull
    @NotBlank
    private String name;           //краткое название
    @NonNull
    @NotBlank
    private String description;    //развернутое описание
    @NonNull
    @NotBlank
    private Boolean available;     //статус о том, доступна или нет вещь для аренды
    private User owner;            //владелец вещи
    private ItemRequest request;          //если вещь была создана по запросу другого пользователя, то в этом поле будет ссылка на запрос
}
