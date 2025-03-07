package ru.practicum.shareit.booking.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public BookingForResponse addBooking(long userId, BookingDtoRequest bookingDtoRequest) {
        Item item = itemRepository.findById(bookingDtoRequest.getItemId()).orElseThrow(() ->
                new ObjectNotFoundException("Вещь с ID " +
                        bookingDtoRequest.getItemId() + " не зарегистрирована!"));

        User user = checkUser(userId);
        validateBooking(bookingDtoRequest, item, user);
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь не доступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Владелец не может создать бронирование");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoRequest, item, user);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        Booking result = bookingRepository.save(booking);
        return BookingMapper.toBookingForResponseMapper(result);
    }

    @Transactional
    @Override
    public BookingForResponse updateBooking(long bookingId, long userId, Boolean approved) {
        Booking booking = checkBooking(bookingId);
        Item item = booking.getItem();
        if (item.getOwner().getId() != userId) {
            throw new BadRequestException("Пользователь не является владельцем вещи " +
                    "и не может подтвердить бронирование");
        }
        checkUser(userId);
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Данное бронирование уже было обработано и имеет статус "
                    + booking.getStatus());
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingForResponseMapper(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingForResponse getBooking(long bookingId, long userId) {
        checkUser(userId);
        checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).filter(booking1 ->
                booking1.getBooker().getId() == userId
                        || booking1.getItem().getOwner().getId() == userId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь не является владельцем вещи " +
                        "и не может подтвердить бронирование"));
        ;
        return BookingMapper.toBookingForResponseMapper(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingForResponse> getAllBookingByUser(String state, long userId, int from, int size) {
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = new ArrayList<>();
        StateBooking stateBooking = StateBooking.getStateFromText(state);
        Pageable pageable = PageRequest.of(from / size, size);

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findAllBookingsByBooker(userId, pageable).getContent();
                break;
            case CURRENT:
                result = bookingRepository.findAllCurrentBookingsByBooker(userId, now, pageable).getContent();
                break;
            case PAST:
                result = bookingRepository.findAllPastBookingsByBooker(userId, now, Status.APPROVED, pageable)
                        .getContent();
                break;
            case FUTURE:
                result = bookingRepository.findAllFutureBookingsByBooker(userId, now, pageable).getContent();
                break;
            case WAITING:
                result = bookingRepository.findAllWaitingBookingsByBooker(userId, Status.WAITING, pageable)
                        .getContent();
                break;
            case REJECTED:
                result = bookingRepository.findAllBookingsByBooker(userId, Status.REJECTED, Status.CANCELED,
                        pageable).getContent();
                break;
        }

        return result.stream().map(BookingMapper::toBookingForResponseMapper)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingForResponse> getAllBookingByOwner(String state, long userId, int from, int size) {
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = new ArrayList<>();
        StateBooking stateBooking = StateBooking.getStateFromText(state);
        Pageable pageable = PageRequest.of(from / size, size);

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findAllBookingsByOwner(userId, pageable).getContent();
                break;
            case CURRENT:
                result = bookingRepository.findAllCurrentBookingsByOwner(userId, now, pageable).getContent();
                break;
            case PAST:
                result = bookingRepository.findAllPastBookingsByOwner(userId, now, Status.APPROVED, pageable)
                        .getContent();
                break;
            case FUTURE:
                result = bookingRepository.findAllFutureBookingsByOwner(userId, now, pageable).getContent();
                break;
            case WAITING:
                result = bookingRepository.findAllWaitingBookingsByOwner(userId, Status.WAITING, pageable)
                        .getContent();
                break;
            case REJECTED:
                result = bookingRepository.findAllBookingsByOwner(userId, Status.REJECTED,
                        Status.CANCELED, pageable).getContent();
                break;
        }

        return result.stream().map(BookingMapper::toBookingForResponseMapper)
                .collect(Collectors.toList());
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID " +
                        userId + " не зарегистрирован!"));
    }

    private Booking checkBooking(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new ObjectNotFoundException("Бронь с ID " +
                        bookingId + " не зарегистрирован!"));
    }

    public void validateBooking(BookingDtoRequest bookingDtoRequest, Item item, User booker) {
        if (item.getOwner().equals(booker)) {
            throw new ObjectNotFoundException("Создать бронь на свою вещь нельзя.");
        }
        List<Booking> bookings = bookingRepository.checkValidateBookings(item.getId(), bookingDtoRequest.getStart());
        if (bookings != null && !bookings.isEmpty()) {
            throw new BadRequestException("Найдено пересечение броней на эту вещь с name = "
                    + item.getName() + ".");
        }
    }
}
