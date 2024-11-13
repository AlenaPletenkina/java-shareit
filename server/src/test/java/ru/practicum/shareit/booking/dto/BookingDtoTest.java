package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.dto.UserWithIdDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingForResponse> json;
    @Autowired
    private JacksonTester<BookingDtoRequest> jsonBookingDtoRequest;

    @Test
    void testBookingForResponse() throws IOException {
        //LocalDateTime dateTime = LocalDateTime.now();

        ItemWithBookingDto itemDto = ItemWithBookingDto.builder()
                .id(1L)
                .name("Sukiyaki")
                .build();


        UserWithIdDto userDto = UserWithIdDto.builder()
                .id(1L)
                .build();

        BookingForResponse bookingDto = BookingForResponse.builder()
                .id(1L)
               // .start(dateTime.plusMinutes(1))
                //.end(dateTime.plusMinutes(2))
                .item(itemDto)
                .booker(userDto)
                .status(Status.WAITING)
                .build();
        JsonContent<BookingForResponse> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        //assertThat(result).extractingJsonPathStringValue("$.start")
               // .isEqualTo(dateTime.plusMinutes(1)
                      //  .toString());
        //assertThat(result).extractingJsonPathStringValue("$.end")
               // .isEqualTo(dateTime.plusMinutes(2)
                      //  .toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("Sukiyaki");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(1);
    }

    @Test
    void testBookingDtoRequest() throws IOException {
        //LocalDateTime dateTime = LocalDateTime.now();

        ItemWithBookingDto itemDto = ItemWithBookingDto.builder()
                .id(1L)
                .name("Sukiyaki")
                .build();

        BookingDtoRequest bookingDto = BookingDtoRequest.builder()
                //.start(dateTime.plusMinutes(1))
               // .end(dateTime.plusMinutes(2))
                .itemId(itemDto.getId())
                .build();
        JsonContent<BookingDtoRequest> result = jsonBookingDtoRequest.write(bookingDto);


        //assertThat(result).extractingJsonPathStringValue("$.start")
            //    .isEqualTo(dateTime.plusMinutes(1)
              //          .toString());
       // assertThat(result).extractingJsonPathStringValue("$.end")
               // .isEqualTo(dateTime.plusMinutes(2)
                      //  .toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
    }
}
