package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDtoSerialize() throws Exception {
        UserDto userDto = new UserDto(1L, "user1", "user@mail.ru");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@mail.ru");
    }

    @Test
    void testUserDtoDeserialize() throws Exception {
        String user = "{\"name\" : \"user1\", \"email\" : \"user@mail.ru\"}";

        UserDto result = json.parseObject(user);

        assertThat(result.getName()).isEqualTo("user1");
        assertThat(result.getEmail()).isEqualTo("user@mail.ru");
    }
}
