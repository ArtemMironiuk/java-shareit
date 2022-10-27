package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class, message = "Поле name не должно быть пустым")
    private String name;
    @Email(message = "Неверно указан email")
    @NotBlank(groups = Create.class, message = "Поле email не должно быть пустым")
    private String email;
}
