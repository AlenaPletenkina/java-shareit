package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingLastAndNextDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserWithIdDto;

@UtilityClass
public class BookingMapper {

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

    public BookingForItemDto toItemBookingInfoDto(Booking booking) {
        return new BookingForItemDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd());
    }

    public BookingForResponse toBookingForResponseMapper(Booking booking) {
        return BookingForResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(new ItemWithBookingDto(booking.getItem().getId(), booking.getItem().getName()))
                .booker(new UserWithIdDto(booking.getBooker().getId()))
                .build();
    }

    public BookingLastAndNextDto toItemBookingLastAndNextDto(Booking booking) {
        return BookingLastAndNextDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}