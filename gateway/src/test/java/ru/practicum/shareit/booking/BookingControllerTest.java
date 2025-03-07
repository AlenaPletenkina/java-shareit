package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = ShareItGateway.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    BookingClient bookingClient;

    @SneakyThrows
    @Test
    void addBookingTest() throws Exception {
        BookingDtoRequest bookingDtoRequest = getBookingDtoRequest(LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(10));
        String bookingJson = objectMapper.writeValueAsString(bookingDtoRequest);
        ResponseEntity<Object> response = new ResponseEntity<>(bookingJson, HttpStatus.OK);
        when(bookingClient.addBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(response);
        String content = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Assertions.assertEquals(bookingJson, content);
    }

    @SneakyThrows
    @Test
    void addBookingWrongTest() throws Exception {
        BookingDtoRequest bookingDtoRequest = getBookingDtoRequest(LocalDateTime.now(), LocalDateTime.now().minusDays(3));
        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Mockito.verify(bookingClient, Mockito.never()).addBooking(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
    }

    @SneakyThrows
    @Test
    void getBookingTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(bookingClient).getBooking(1, 1L);
    }

    @SneakyThrows
    @Test
    void updatedBookingTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(bookingClient).updateBooking(1L, 1L, true);
    }

    @SneakyThrows
    @Test
    void getAllBookingByOwnerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(bookingClient).getAllBookingByOwner(1L, BookingState.valueOf("ALL"),
                0, 20);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state={state}", "UNICE")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state={state}", "WAITING")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(bookingClient).getAllBookingByOwner(1L,
                BookingState.valueOf("WAITING"), 0, 20);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?from={from}&size={size}",
                                -1, 0)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingClient, Mockito.never()).getAllBookingByOwner(1L,
                BookingState.valueOf("ALL"), -1, 0);
    }

    @SneakyThrows
    @Test
    void getAllBookingByUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(bookingClient).getAllBookingByUser(1, BookingState.valueOf("ALL"),
                0, 20);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings?state={state}", "WAITING")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(bookingClient).getAllBookingByUser(1,
                BookingState.valueOf("WAITING"), 0, 20);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings?from={from}&size={size}", -1, 0)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest());
        Mockito.verify(bookingClient, Mockito.never()).getAllBookingByUser(1, BookingState.valueOf("ALL"),
                -1, 0);
    }

    BookingDtoRequest getBookingDtoRequest(LocalDateTime start, LocalDateTime end) {
        return new BookingDtoRequest(
                start,
                end, 1L
        );
    }
}
