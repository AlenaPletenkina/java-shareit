package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.services.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;
    static final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoResponse addBooking(@RequestHeader(userHeader) long userId,
                                         @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info("POST запрос на создание бронирования");
        return service.addBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{booking-id}")
    public BookingDtoResponse updateBooking(@PathVariable("booking-id") long bookingId, @RequestHeader(userHeader) long userId,
                                            @RequestParam Boolean approved) {
        log.info("Patch запрос на обновление бронирования");
        return service.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{booking-id}")
    public BookingDtoResponse getBooking(@PathVariable("booking-id") long bookingId, @RequestHeader(userHeader) long userId) {
        log.info("Get запрос на получение бронирования");
        return service.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingByUser(@RequestParam(defaultValue = "ALL") String state,
                                                        @RequestHeader(userHeader) long userId) {
        log.info("Get запрос на получение списка бронирования пользователя с Id {} со статусом {}", userId, state);
        return service.getAllBookingByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                         @RequestHeader(userHeader) long userId) {
        log.info("Get запрос на получение бронирований владельца с Id {} со статусом {}", userId, state);
        return service.getAllBookingByOwner(state, userId);
    }
}
