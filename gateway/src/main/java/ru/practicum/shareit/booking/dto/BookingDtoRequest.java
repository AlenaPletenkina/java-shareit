package ru.practicum.shareit.booking.dto;


import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.validation.StartBeforeEndDateValid;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEndDateValid
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoRequest {
    @FutureOrPresent
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
}
