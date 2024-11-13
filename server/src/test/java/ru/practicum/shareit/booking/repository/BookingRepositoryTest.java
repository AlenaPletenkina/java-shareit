package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    protected TestEntityManager entityManager;

    public static User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    public static Item makeItem(Long id, String name, String description, User user, boolean available) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(user);
        item.setAvailable(available);
        return item;
    }

    public static Booking makeBooking(
            Long id,
            LocalDateTime start,
            LocalDateTime end,
            Item item,
            User user,
            Status status
    ) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(status);
        return booking;
    }

    @Test
    public void shouldFindNoBookingsIfRepositoryIsEmptyTest() {
        Iterable<Booking> bookings = bookingRepository.findAll();

        assertThat(bookings).isEmpty();
    }

    @Test
    public void shouldStoreBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Booking booking = bookingRepository.save(makeBooking(null,
                start,
                end,
                item,
                booker,
                Status.WAITING));

        assertThat(booking)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("status", Status.WAITING)
                .hasFieldOrProperty("item")
                .hasFieldOrProperty("booker");
        assertThat(booking.getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldFindAllBookingsByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                booker,
                Status.WAITING));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.REJECTED));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllBookingsByOwner(owner.getId(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Ultra Ball");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldCurrentByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(1),
                now.plusDays(1),
                item1,
                booker,
                Status.WAITING));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllCurrentBookingsByOwner(owner.getId(),
                LocalDateTime.now(),pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldFindPastByOwnerTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner1 = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User owner2 = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner1,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner2,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                owner1,
                Status.APPROVED));
        entityManager.persist(makeBooking(null,
                now.minusDays(3),
                now.minusDays(2),
                item2,
                owner2,
                Status.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllPastBookingsByOwner(owner1.getId(),
                LocalDateTime.now(), Status.APPROVED, pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldFindFutureByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                booker,
                Status.WAITING));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllFutureBookingsByOwner(owner.getId(),
                LocalDateTime.now(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Ultra Ball");
    }

    @Test
    public void shouldFindWaitingByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                booker,
                Status.WAITING));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllWaitingBookingsByOwner(owner.getId(),
                Status.WAITING, pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Ultra Ball");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldFindRegectedByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                booker,
                Status.CANCELED));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.REJECTED));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllBookingsByOwner(owner.getId(),
                Status.REJECTED, Status.CANCELED, pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Ultra Ball");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldFindAllBookingsByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                booker,
                Status.WAITING));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.APPROVED));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllBookingsByBooker(booker.getId(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Ultra Ball");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldCurrentByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(1),
                now.plusDays(1),
                item1,
                booker,
                Status.WAITING));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllCurrentBookingsByBooker(booker.getId(),
                LocalDateTime.now(),pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldFindPastByBookerTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                booker,
                Status.APPROVED));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllPastBookingsByBooker(booker.getId(),
                LocalDateTime.now(), Status.APPROVED, pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldFindFutureByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                booker,
                Status.WAITING));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllFutureBookingsByBooker(booker.getId(),
                LocalDateTime.now(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Ultra Ball");
    }

    @Test
    public void shouldFindWaitingByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                booker,
                Status.WAITING));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllWaitingBookingsByBooker(booker.getId(),
                Status.WAITING, pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Ultra Ball");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldFindRegectedByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.minusDays(1),
                item1,
                booker,
                Status.CANCELED));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.REJECTED));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllBookingsByBooker(booker.getId(),
                Status.REJECTED, Status.CANCELED, pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Ultra Ball");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "Poke Ball");
    }

    @Test
    public void shouldValidateBookingTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null,
                "Ash",
                "ash@gmail.com"));
        User booker = entityManager.persist(makeUser(null,
                "Misty",
                "misty@gmail.com"));
        Item item1 = entityManager.persist(makeItem(null,
                "Poke Ball",
                "The Poke Ball is a sphere",
                owner,
                true));
        Item item2 = entityManager.persist(makeItem(null,
                "Ultra Ball",
                "is a Poke Ball that has a 2x catch rate modifier",
                owner,
                true));
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest
                .builder()
                .itemId(owner.getId())
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build();
        entityManager.persist(makeBooking(null,
                now.minusDays(2),
                now.plusDays(1),
                item1,
                booker,
                Status.WAITING));
        entityManager.persist(makeBooking(null,
                now.plusDays(1),
                now.plusDays(2),
                item2,
                booker,
                Status.WAITING));

        try {
            bookingRepository.checkValidateBookings(item1.getId(), bookingDtoRequest.getStart());
        } catch (BadRequestException ex) {
            assertThatExceptionOfType(BadRequestException.class);
        }
    }
}