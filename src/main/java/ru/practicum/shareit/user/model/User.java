package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Positive
    private Long id;//уникальный идентификатор
    @NonNull
    @NotBlank
    private String name;  //имя или логин пользователя
    @NonNull
    @NotBlank
    @Email
    private String email; //адрес электронной почты (два пользователя не моуг иметь одинаковый адрес электронной почты)
}
