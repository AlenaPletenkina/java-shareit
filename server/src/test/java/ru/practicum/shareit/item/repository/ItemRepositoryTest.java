package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    User user;
    Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("userName1")
                .email("test@mail.fg")
                .build();
        userRepository.save(user);
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
    }

    @Test
    void testFindAllByOwnerOrderById() {
        List<Item> itemList = itemRepository
                .findAllByOwnerIdOrderById(user.getId(), PageRequest.of(0, 2)).getContent();

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void testSearchItemsByText() {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        List<Item> itemList =
                itemRepository.findByNameOrDescription("oh", pageable).getContent();

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    public void testGetAllItems_withBlankText_shouldReturnEmptyList() {
        String text = "text";
        Pageable page = PageRequest.of(0, 10);

        Page<Item> actualResult = itemRepository.findByNameOrDescription(text, page);
        assertEquals(List.of(), actualResult.getContent());
    }

    @Test
    public void testFindByRequestIdIn() {
        List<Item> actualResult = itemRepository.findByRequestIdIn(List.of(user.getId()));

        assertNotNull(actualResult);
        assertEquals(0, actualResult.size());
    }

    @Test
    public void testFindAllByRequestId() {
        itemRepository.save(Item.builder()
                .name("Cook")
                .description("Soda")
                .available(true)
                .owner(user)
                .build());
        List<Item> actualResult = itemRepository.findAllByRequestId(user.getId());

        assertNotNull(actualResult);
        assertEquals(0, actualResult.size());
    }
}
