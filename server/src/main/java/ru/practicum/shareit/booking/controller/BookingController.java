package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingForResponse;
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

    @PostMapping
    public BookingForResponse addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info("POST запрос на создание брони");
        return service.addBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingForResponse updateBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam Boolean approved) {
        log.info("Patch запрос на обновление брони");
        return service.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingForResponse getBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get запрос на получение брони");
        return service.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingForResponse> getAllBookingByUser(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Get запрос на получение листа бронирования user с Id {} со статусом {}", userId, state);
        return service.getAllBookingByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingForResponse> getAllBookingByOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Get запрос на получение бронирования броней owner с Id {} со статусом {}", userId, state);
        return service.getAllBookingByOwner(state, userId, from, size);
    }
}