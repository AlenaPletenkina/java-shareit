package ru.practicum.shareit.booking.dto;


import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import ru.practicum.shareit.booking.validation.StartBeforeEndDateValid;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEndDateValid
@Builder
public class BookingDtoRequest {
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
