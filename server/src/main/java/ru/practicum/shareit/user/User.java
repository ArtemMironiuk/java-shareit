package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @Column(name = "user_name", nullable = false)
    private String name;  //имя или логин пользователя
    @Column(name = "email", nullable = false, unique = true, length = 512)
    private String email; //адрес электронной почты (два пользователя не моуг иметь одинаковый адрес электронной почты)
}
