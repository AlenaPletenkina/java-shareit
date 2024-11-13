package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForItemRequestResponseDto;
import ru.practicum.shareit.user.dto.UserForItemRequestDto;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestResponseDto> json;

    @Test
    void testItemDto() throws IOException {
        //LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ItemForItemRequestResponseDto itemDto = ItemForItemRequestResponseDto.builder()
                .id(1L)
                .name("Poke Ball")
                .description("The Poke Ball is a sphere")
                .available(true)
                .build();

        UserForItemRequestDto userDto =  UserForItemRequestDto.builder()
                .id(1L)
                .name("Ash")
                .build();

        ItemRequestResponseDto requestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Some items for fun")
                .requester(userDto)
                //.created(dateTime)
                .build();

        itemDto.setRequestId(requestResponseDto.getId());
        requestResponseDto.setItems(List.of(itemDto));
        JsonContent<ItemRequestResponseDto> result = json.write(requestResponseDto);
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Some items for fun");
       // assertThat(result).extractingJsonPathStringValue("$.created")
              //  .isEqualTo(dateTime.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.requester.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Poke Ball");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo("The Poke Ball is a sphere");
    }
}
