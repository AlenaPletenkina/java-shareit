package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemInAndStatus(
            List<Item> items,
            Status status,
            Sort created);

    @Query
            ("select b from Booking b " +
                    "where b.item.id = ?1")
    List<Booking> findAllBookingsByItem(
            Long itemId);

    @Query
            ("select new java.lang.Boolean(COUNT(b) > 0) from Booking b " +
                    "where (b.item.id = ?1 " +
                    "and b.status = ?3 " +
                    "and b.end = ?4 " +
                    "or b.end < ?4) " +
                    "and b.booker.id = ?2")
    Boolean checkValidateBookingsFromItemAndStatus(
            Long itemId, Long userId, Status status, LocalDateTime end);

    @Query
            ("select b from Booking b " +
                    "where b.item.id = ?1 " +
                    "and ?2 between b.start and b.end")
    List<Booking> checkValidateBookings(
            Long itemId, LocalDateTime bookingDtoStartIsBeforeOrAfter);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "order by b.start DESC")
    Page<Booking> findAllBookingsByBooker(
            Long userId, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and ?2 between b.start and b.end " +
                    "order by b.start DESC")
    Page<Booking> findAllCurrentBookingsByBooker(
            Long userId, LocalDateTime now, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and ?2 > b.end " +
                    "and b.status = ?3 " +
                    "order by b.start DESC")
    Page<Booking> findAllPastBookingsByBooker(
            Long userId,
            LocalDateTime now, Status status, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and b.start > ?2 " +
                    "order by b.start DESC")
    Page<Booking> findAllFutureBookingsByBooker(
            Long userId,
            LocalDateTime now, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and b.status = ?2 " +
                    "order by b.start DESC")
    Page<Booking> findAllWaitingBookingsByBooker(
            Long userId,
            Status status, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and b.status = ?2 " +
                    "or b.status = ?3 " +
                    "order by b.start DESC")
    Page<Booking> findAllBookingsByBooker(
            Long userId,
            Status status, Status st, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "order by b.start DESC")
    Page<Booking> findAllBookingsByOwner(
            Long userId, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and ?2 between b.start and b.end " +
                    "order by b.start DESC")
    Page<Booking> findAllCurrentBookingsByOwner(
            Long userId,
            LocalDateTime now, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and ?2 > b.end " +
                    "and b.status = ?3 " +
                    "order by b.start DESC")
    Page<Booking> findAllPastBookingsByOwner(
            Long userId,
            LocalDateTime now, Status status, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and b.start > ?2 " +
                    "order by b.start DESC")
    Page<Booking> findAllFutureBookingsByOwner(
            Long userId,
            LocalDateTime now, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and b.status = ?2 " +
                    "order by b.start DESC")
    Page<Booking> findAllWaitingBookingsByOwner(
            Long userId,
            Status status, Pageable pageable);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and b.status = ?2 " +
                    "or b.status = ?3 " +
                    "order by b.start DESC")
    Page<Booking> findAllBookingsByOwner(
            Long userId,
            Status status, Status st, Pageable pageable);
}