package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.booking.services.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toItemBookingInfoDto;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BookingServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final BookingServiceImpl bookingServiceImpl;
    private UserDto testUser;
    private UserDto secondTestUser;
    private ItemDtoResponse itemDtoFromDB;
    private BookingDtoRequest bookItemRequestDto;
    private BookingDtoRequest secondBookItemRequestDto;
    User user;
    Item item;
    private ItemDtoRequest itemAvailableFalseDto;
    Item itemAvailableFalse;

    UserDto owner;
    UserDto booker;
    ItemDtoRequest itemDtoToCreate;
    BookingDtoRequest bookingToCreate;

    @BeforeEach
    public void setUp() {

        ItemDtoRequest itemDto = ItemDtoRequest.builder()
                .name("Doll")
                .description("Barbie")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .name("Alena")
                .email("alena@gmail.com")
                .build();

        UserDto secondUserDto = UserDto.builder()
                .name("Nasty")
                .email("nasty@gmail.com")
                .build();

        testUser = userService.createUser(userDto);
        user = User.builder()
                .id(1L)
                .name("Alena")
                .email("alena@gmail.com")
                .build();
        secondTestUser = userService.createUser(secondUserDto);
        itemDtoFromDB = itemService.addItem(testUser.getId(), itemDto);

        item = Item.builder()
                .id(1L)
                .name("Doll")
                .description("Barbie")
                .available(true)
                .owner(user)
                .build();
        itemAvailableFalseDto = ItemDtoRequest.builder()
                .id(2L)
                .name("Ball")
                .description("description Ball")
                .available(false)
                .requestId(user.getId())
                .build();

        itemAvailableFalse = Item.builder()
                .id(2L)
                .name("Ball")
                .description("description Ball")
                .available(false)
                .owner(user)
                .build();


        bookItemRequestDto = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusNanos(1))
                .end(LocalDateTime.now().plusNanos(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        secondBookItemRequestDto = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusHours(5))
                .end(LocalDateTime.now().plusHours(10))
                .itemId(itemDtoFromDB.getId())
                .build();

        owner = new UserDto(null, "testUser", "test@email.com");
        booker = new UserDto(null, "testUser2", "test2@email.com");
        itemDtoToCreate = ItemDtoRequest.builder().name("testItem").description("testDescription").available(true).build();
        bookingToCreate = BookingDtoRequest.builder().itemId(1L).start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2)).build();
    }

    @SneakyThrows
    @Test
    void checkRequestTest() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addItem(user.getId(), itemAvailableFalseDto));
        assertEquals("Запрос не найден", ex.getMessage());
    }

    @SneakyThrows
    @Test
    void createBookingTest() {
        BookingForResponse addBooking = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);

        assertNotNull(addBooking);
        checkBookings(addBooking, bookItemRequestDto, secondTestUser, itemDtoFromDB, Status.WAITING);
    }

    @SneakyThrows
    @Test
    void updateBookingTest() {
        BookingForResponse bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingForResponse approveBooking = bookingService.updateBooking(bookingDtoFromDB.getId(), testUser.getId(),
                true);

        assertNotNull(approveBooking);
        checkBookings(approveBooking, bookItemRequestDto, secondTestUser, itemDtoFromDB, Status.APPROVED);
    }

    @Test
    void updateBookingForStatusApprovedTest() {
        BookingForResponse bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingForResponse waitingBooking = bookingService.updateBooking(bookingDtoFromDB.getId(), testUser.getId(),
                true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bookingService.updateBooking(testUser.getId(), bookingDtoFromDB.getId(),
                        true));
        assertEquals("Данное бронирование уже было обработано и имеет статус APPROVED", ex.getMessage());
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        BookingForResponse bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingForResponse getBooking = bookingService.getBooking(bookingDtoFromDB.getId(), secondTestUser.getId());

        assertNotNull(getBooking);
        checkBookings(getBooking, bookItemRequestDto, secondTestUser, itemDtoFromDB, Status.WAITING);
    }

    @Test
    void getBookingByIdTestException() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBooking(999L, 1L));
        assertEquals("Бронь с ID 999 не зарегистрирован!", ex.getMessage());
    }

    @SneakyThrows
    @Test
    void getAllBookingsTest() {
        BookingForResponse bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingForResponse bookingDtoFromDB2 = bookingService.addBooking(secondTestUser.getId(), secondBookItemRequestDto);
        List<BookingForResponse> bookingDtos = List.of(bookingDtoFromDB, bookingDtoFromDB2);
        List<BookingForResponse> bookings = bookingService.getAllBookingByUser("ALL",
                secondTestUser.getId(), 0, 3);

        assertNotNull(bookings);
        assertEquals(bookings.size(), bookingDtos.size());

    }

    @Test
    void getAllBookingsExceptionTest() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getAllBookingByUser("ALL",
                        3L, 0, 3));
        assertEquals("Пользователь с ID 3 не зарегистрирован!", ex.getMessage());
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookingsTest() {
        BookingForResponse bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingForResponse bookingDtoFromDB2 = bookingService.addBooking(secondTestUser.getId(), secondBookItemRequestDto);
        List<BookingForResponse> bookingDtos = List.of(bookingDtoFromDB, bookingDtoFromDB2);
        List<BookingForResponse> bookings = bookingService
                .getAllBookingByOwner("ALL", testUser.getId(), 0, 3);

        assertNotNull(bookings);
        assertEquals(bookings.size(), bookingDtos.size());
    }

    @SneakyThrows
    @Test
    void approveBookingWrongOwnerTest() {
        BookingForResponse bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bookingService.updateBooking(bookingDtoFromDB.getId(), secondTestUser.getId(), true));
        assertEquals("Пользователь не является владельцем вещи и не может подтвердить бронирование", ex.getMessage());
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookingsRejectedStateTest() {
        BookingForResponse bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingForResponse bookingDtoFromDB2 = bookingService.updateBooking(bookingDtoFromDB.getId(),
                testUser.getId(), false);
        List<BookingForResponse> bookings = bookingService
                .getAllBookingByOwner("REJECTED", testUser.getId(), 0, 3);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), Status.REJECTED);
    }

    @SneakyThrows
    @Test
    void getAllBookingsCurrentStateTest() {
        BookingDtoRequest bookingDto = BookingDtoRequest.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingDtoRequest> bookingDtos = List.of(bookingDto);
        BookingForResponse firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        bookingService.updateBooking(testUser.getId(), firstBooking.getId(), true);
        List<BookingForResponse> currentBookings = bookingService.getAllBookingByUser("CURRENT",
                secondTestUser.getId(), 0, 3);
        BookingForResponse currentBooking = currentBookings.get(0);

        assertEquals(currentBookings.size(), bookingDtos.size());
    }

    @SneakyThrows
    @Test
    void getAllBookingsFutureStateTest() {
        BookingDtoRequest bookingDto = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingDtoRequest> bookingDtos = List.of(bookingDto);
        BookingForResponse firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        List<BookingForResponse> futureBookings = bookingService.getAllBookingByUser("FUTURE",
                secondTestUser.getId(), 0, 3);
        BookingForResponse futureBooking = futureBookings.get(0);

        assertEquals(futureBookings.size(), bookingDtos.size());
        assertEquals(futureBooking.getId(), firstBooking.getId());
    }

    @SneakyThrows
    @Test
    void getAllBookingsPastStateTest() {
        BookingDtoRequest bookingDto = BookingDtoRequest.builder()
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingDtoRequest> bookingDtos = List.of(bookingDto);
        BookingForResponse firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        bookingService.updateBooking(testUser.getId(), firstBooking.getId(), true);
        List<BookingForResponse> pastBookings = bookingService.getAllBookingByUser("PAST",
                secondTestUser.getId(), 0, 3);
        BookingForResponse pastBooking = pastBookings.get(0);

        assertEquals(pastBookings.size(), bookingDtos.size());
        assertEquals(pastBooking.getId(), firstBooking.getId());
    }


    @SneakyThrows
    @Test
    void getAllOwnerBookingsFutureStateTest() {
        BookingDtoRequest bookingDto = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingDtoRequest> bookingDtos = List.of(bookingDto);
        BookingForResponse firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        bookingService.updateBooking(testUser.getId(), firstBooking.getId(), true);
        List<BookingForResponse> futureBookings = bookingService.getAllBookingByOwner("FUTURE",
                testUser.getId(), 0, 3);
        BookingForResponse futureBooking = futureBookings.get(0);

        assertEquals(futureBookings.size(), bookingDtos.size());
        assertEquals(futureBooking.getId(), firstBooking.getId());
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookingsPastStateTest() {
        BookingDtoRequest bookingDto = BookingDtoRequest.builder()
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingDtoRequest> bookingDtos = List.of(bookingDto);
        BookingForResponse firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        bookingService.updateBooking(testUser.getId(), firstBooking.getId(), true);

        List<BookingForResponse> pastBookings = bookingService.getAllBookingByOwner("PAST",
                testUser.getId(), 0, 3);
        BookingForResponse pastBooking = pastBookings.get(0);

        assertEquals(pastBookings.size(), bookingDtos.size());
        assertEquals(pastBooking.getId(), firstBooking.getId());
    }

    @Test
    public void checkDatesNegativeTestCaseTest() {
        BookingDtoRequest bookingDto = BookingDtoRequest.builder()
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .itemId(itemDtoFromDB.getId())
                .build();

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class,
                () -> bookingServiceImpl.validateBooking(bookingDto, item, user));
        assertEquals("Создать бронь на свою вещь нельзя.", ex.getMessage());
    }

    @Test
    public void toItemBookingForResponsePositiveTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(User.builder().id(2L).build())
                .item(Item.builder().id(1L).name("Hole").build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        BookingForResponse itemBookingInfoDto = BookingMapper.toBookingForResponseMapper(booking);

        assertEquals(1L, itemBookingInfoDto.getId());
        assertEquals(2L, itemBookingInfoDto.getBooker().getId());
        assertEquals(1L, itemBookingInfoDto.getItem().getId());
        assertEquals("Hole", itemBookingInfoDto.getItem().getName());
        assertEquals(booking.getStart(), itemBookingInfoDto.getStart());
        assertEquals(booking.getEnd(), itemBookingInfoDto.getEnd());
    }

    @Test
    public void toItemBookingForResponseNegativeTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(User.builder().id(2L).build())
                .item(Item.builder().id(1L).name("Hole").build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        BookingForResponse itemBookingInfoDto = BookingMapper.toBookingForResponseMapper(booking);

        assertNotEquals(2L, itemBookingInfoDto.getId());
        assertNotEquals(1L, itemBookingInfoDto.getBooker().getId());
        assertNotEquals(2L, itemBookingInfoDto.getItem().getId());
        assertNotEquals("Hol", itemBookingInfoDto.getItem().getName());
        assertNotEquals(booking.getStart().plusDays(1), itemBookingInfoDto.getStart());
        assertNotEquals(booking.getEnd().minusHours(2), itemBookingInfoDto.getEnd());
    }

    private void checkBookings(BookingForResponse booking, BookingDtoRequest secondBooking,
                               UserDto user, ItemDtoResponse item, Status status) {
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(status));
        assertThat(booking.getStart(), equalTo(secondBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(secondBooking.getEnd()));
        assertThat(booking.getBooker().getId(), equalTo(user.getId()));
        assertThat(booking.getItem().getId(), equalTo(item.getId()));
        assertThat(booking.getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void bookerNotAvailableItem2Test() {
        UserDto createdBooker = userService.createUser(booker);
        BookingDtoRequest bookDto = new BookingDtoRequest(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                2L
        );
        Exception exception = assertThrows(ObjectNotFoundException.class, ()
                -> bookingService.addBooking(createdBooker.getId(), bookDto));
        assertEquals("Вещь с ID 2 не зарегистрирована!", exception.getMessage());
    }

    @Test
    void addBookingItemAvailableFalseTest() {
        UserDto createdBooker = userService.createUser(booker);
        BookingDtoRequest bookDto = new BookingDtoRequest(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                itemAvailableFalse.getId()
        );
        Exception exception = assertThrows(ObjectNotFoundException.class, ()
                -> bookingService.addBooking(createdBooker.getId(), bookDto));
        assertEquals("Вещь с ID 2 не зарегистрирована!", exception.getMessage());
    }

    @Test
    public void toItemBookingInfoDtoPositiveTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(User.builder().id(2L).build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        BookingForItemDto itemBookingInfoDto = toItemBookingInfoDto(booking);

        assertEquals(1L, itemBookingInfoDto.getId());
        assertEquals(2L, itemBookingInfoDto.getBookerId());
        assertEquals(booking.getStart(), itemBookingInfoDto.getStart());
        assertEquals(booking.getEnd(), itemBookingInfoDto.getEnd());
    }

    @Test
    public void toItemBookingInfoDtoTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(User.builder().id(2L).build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        BookingForItemDto itemBookingInfoDto = toItemBookingInfoDto(booking);

        assertNotEquals(2L, itemBookingInfoDto.getId());
        assertNotEquals(1L, itemBookingInfoDto.getBookerId());
        assertNotEquals(booking.getStart(), itemBookingInfoDto.getEnd());
        assertNotEquals(booking.getEnd(), itemBookingInfoDto.getStart());
    }

    @Test
    public void approveWithInvalidOwnerIdShouldThrowNotFoundExceptionTest() {
        Long ownerId = 3L;
        Long bookingId = 2L;
        boolean approved = true;

        Exception exception = assertThrows(ObjectNotFoundException.class, () -> bookingService.updateBooking(ownerId, bookingId, approved));

        assertEquals("Бронь с ID 3 не зарегистрирован!", exception.getMessage());
    }

    @Test
    public void approveWithInvalidBookingIdShouldThrowNotFoundExceptionTest() {
        Long ownerId = 1L;
        Long bookingId = 4L;
        boolean approved = true;

        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.updateBooking(ownerId, bookingId, approved));

        assertEquals("Бронь с ID 1 не зарегистрирован!", exception.getMessage());
    }
}