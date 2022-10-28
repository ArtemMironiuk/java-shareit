package ru.practicum.shareit.handler;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
