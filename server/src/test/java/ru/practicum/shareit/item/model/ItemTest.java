package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ItemTest {
    User user = User.builder()
            .id(1L)
            .name("userName1")
            .email("test@mail.fg")
            .build();
    Item item = Item.builder()
            .id(1L)
            .name("item1")
            .description("item 1 Oh")
            .available(true)
            .owner(user)
            .build();
    Item item2 = Item.builder()
            .id(1L)
            .name("item1")
            .description("item 1 Oh")
            .available(true)
            .owner(user)
            .build();
    Item item3 = Item.builder()
            .id(1L)
            .name("item1")
            .description("item 1 Oh")
            .available(false)
            .owner(user)
            .build();

    @Test
    void testItemHashCode() {
        assertEquals(item, item2);
        assertNotEquals(item, item3);
    }
}
