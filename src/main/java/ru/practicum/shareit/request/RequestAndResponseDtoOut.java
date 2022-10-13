package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestAndResponseDtoOut {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<Response> response;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private ItemDto item;
        private UserDto user;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ItemDto {
            private Long id;
            private String name;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class UserDto {
            private Long id;
        }
    }
}
