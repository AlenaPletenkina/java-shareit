package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDtoResponse> json;
    @Autowired
    private JacksonTester<ItemDtoRequest> jsonItemDtoRequest;

    @Test
    void testItemDtoRequest() throws IOException {
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("ball")
                .description("for playing with a ball")
                .available(true)
                .build();
        JsonContent<ItemDtoRequest> result = jsonItemDtoRequest.write(itemDtoRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("ball");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("for playing with a ball");
    }

    @Test
    void testItemDtoResponse() throws IOException {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(1L)
                .name("ball")
                .description("for playing with a ball")
                .available(true)
                .build();
        JsonContent<ItemDtoResponse> result = json.write(itemDtoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("ball");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("for playing with a ball");
    }
}
