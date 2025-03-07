package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;
    @Autowired
    private JacksonTester<UserDto> jsonUserDtoRequest;

    @Test
    void itemDtoRequestTest() throws IOException {
        UserDto userDtoRequest = UserDto.builder()
                .id(1L)
                .name("Alena")
                .email("alena@mail.ru")
                .build();
        JsonContent<UserDto> result = jsonUserDtoRequest.write(userDtoRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Alena");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("alena@mail.ru");
    }

    @Test
    void itemDtoResponseTest() throws IOException {
        UserDto userDtoResponse = UserDto.builder()
                .id(1L)
                .name("Alena")
                .email("alena@mail.ru")
                .build();
        JsonContent<UserDto> result = json.write(userDtoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Alena");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("alena@mail.ru");
    }
}
