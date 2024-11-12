package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.dto.UserWithIdDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BookingForResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemWithBookingDto item;
    private UserWithIdDto booker;
    private Status status;
}