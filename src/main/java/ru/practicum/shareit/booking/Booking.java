package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "booking", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                //уникальный идентификатор бронирования
    @Column(name = "start_booking",nullable = false)
    private LocalDateTime start;    //дата и время начала бронирования
    @Column(name = "end_booking")
    private LocalDateTime end;      //дата и время конца бронирования
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;              //вещь, которую пользователь бронирует
    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;            //пользователь, который осуществляет бронирование
    @Enumerated(EnumType.STRING)
    private StatusBooking status;         //статус бронирования(WAITING-новое бронирование, ожидает одобрения,
                                    //APPROVED-бронирование подтверждено владельцем, REJECTED-бронирование отклонено владельцем,
                                    //CANCELED-бронирование отменено создателем).
}
