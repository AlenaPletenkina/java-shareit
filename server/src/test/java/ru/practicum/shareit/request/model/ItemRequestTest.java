package ru.practicum.shareit.request.model;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
public class ItemRequestTest {
    User user = User.builder()
            .id(1L)
            .name("userName1")
            .email("test@mail.fg")
            .build();
    Item item = Item.builder()
            .name("item1")
            .description("item 1 Oh")
            .available(true)
            .owner(user)
            .build();
    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("read book")
            .requester(user)
            .created(null)
            .build();
    ItemRequest itemRequest2 = ItemRequest.builder()
            .id(1L)
            .description("read book")
            .requester(user)
            .created(null)
            .build();
    ItemRequest itemRequest3 = ItemRequest.builder()
            .id(1L)
            .description("ball")
            .requester(user)
            .created(LocalDateTime.now())
            .build();

    @Test
    void testItemRequestHashCode() {
        assertEquals(itemRequest, itemRequest2);
        assertNotEquals(itemRequest, itemRequest3);
    }
}
