package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.ObjectNotFoundException;

public enum StateBooking {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static StateBooking getStateFromText(String text) {
        for (StateBooking state : StateBooking.values()) {
            if (state.toString().equals(text)) {
                return state;
            }
        }
        throw new ObjectNotFoundException("Неизвестный статус");
    }
}
