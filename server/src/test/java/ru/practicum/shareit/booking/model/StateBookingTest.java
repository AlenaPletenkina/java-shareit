package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StateBookingTest {

    @Test
    void testStateBooking() {
        assertEquals(StateBooking.getStateFromText("ALL"), StateBooking.ALL);
        assertEquals(StateBooking.getStateFromText("PAST"), StateBooking.PAST);
        assertEquals(StateBooking.getStateFromText("CURRENT"), StateBooking.CURRENT);
        assertEquals(StateBooking.getStateFromText("FUTURE"), StateBooking.FUTURE);
        assertEquals(StateBooking.getStateFromText("REJECTED"), StateBooking.REJECTED);
        assertEquals(StateBooking.getStateFromText("WAITING"), StateBooking.WAITING);
    }
}
