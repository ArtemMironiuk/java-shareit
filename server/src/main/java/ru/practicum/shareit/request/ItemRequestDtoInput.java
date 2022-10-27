package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import ru.practicum.shareit.Create;
//
//import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDtoInput {
//    @NotBlank(groups = Create.class)
    private String description;
}
