package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOutputItem;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoOutputItem lastBooking;
    private BookingDtoOutputItem nextBooking;
    private List<CommentDto> commentsDto;

//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class BookingDto {
//        Long id;
////        LocalDateTime start;
////        LocalDateTime end;
//        Long bookerId;
//    }
}
