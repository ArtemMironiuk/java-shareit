package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;//уникальный идентификатор
    @NotBlank
    @Column(name = "user_name", nullable = false)
    private String name;  //имя или логин пользователя
    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true,length = 512)
    private String email; //адрес электронной почты (два пользователя не моуг иметь одинаковый адрес электронной почты)
}
