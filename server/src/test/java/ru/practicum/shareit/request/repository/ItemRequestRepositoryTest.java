package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    User user;
    User user1;
    Item item;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("userName1")
                .email("test@mail.fg")
                .build();
        user1 = User.builder()
                .name("userName2")
                .email("test2@mail.fg")
                .build();
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(Item.builder()
                .name("item1")
                .description("item 1 Oh")
                .available(true)
                .owner(user)
                .build());
        itemRepository.save(Item.builder()
                .name("Boook")
                .description("Soha")
                .available(true)
                .owner(user)
                .build());
        itemRequestRepository.save(ItemRequest.builder()
                .description("read book")
                .requester(user1)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void testfindAllByNotRequesterId() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByNotRequesterId(user.getId(), PageRequest.of(0, 2)).getContent();

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
    }

    @Test
    void testFindAll() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAll(PageRequest.of(0, 2)).getContent();

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
    }

    @Test
    public void testFindItemRequestsByUserIdt() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findItemRequestsByUserId(user1.getId());

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
    }
}
