package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.services.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ItemServiceImplTest {
    LocalDateTime now = LocalDateTime.now();
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getItemWithBookingAndCommentTest() {
        CommentDtoRequest inputCommentDto = CommentDtoRequest.builder()
                .text("new comment for test")
                .build();

        User owner2 = User.builder()
                .id(2L)
                .name("name for owner")
                .email("owner2@aadmf.wreew")
                .build();

        User userForTest2 = User.builder()
                .id(1L)
                .name("name user for test 2")
                .email("userForTest2@ahd.ew")
                .build();

        Item zaglushka = Item.builder()
                .id(1L)
                .name("zaglushka")
                .description("desc item zaglushka")
                .owner(owner2).build();

        Booking bookingFromBd = Booking.builder()
                .id(1L)
                .item(zaglushka)
                .booker(userForTest2)
                .start(now.minusDays(10))
                .end(now.minusDays(5))
                .build();

        Item itemFromBd = Item.builder()
                .id(1L)
                .name("name for item")
                .description("desc for item")
                .owner(owner2)
                .available(true)
                .build();

        CommentDtoResponse commentDto = CommentDtoResponse.builder()
                .id(1L)
                .text("comment 1")
                .authorName("name user for test 2")
                .created(now.minusDays(5))
                .build();

        Comment outputComment = Comment.builder()
                .id(1L)
                .author(userForTest2)
                .text("comment 1")
                .item(itemFromBd)
                .build();

        UserRepository userRepositoryJpa2 = mock(UserRepository.class);
        ItemRepository itemRepositoryJpa2 = mock(ItemRepository.class);
        CommentRepository commentRepository2 = mock(CommentRepository.class);

        when(userRepositoryJpa2.findById(any())).thenReturn(Optional.of(userForTest2));
        when(itemRepositoryJpa2.findById(any())).thenReturn(Optional.of(itemFromBd));
        when(commentRepository2.save(any())).thenReturn(outputComment);
    }

    @Test
    public void addItemTest() {
        UserDto userDto = new UserDto(1L,
                "name",
                "mail@gmail.com"
        );
        userService.createUser(userDto);

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("mail@gmail.com")
                .build();

        UserDto userDtoResponse = UserMapper.toUserDtoResponse(user);

        ItemDtoRequest itemDtoRequest1 = ItemDtoRequest.builder()
                .id(1L)
                .name("name for item 1")
                .description("description for item 1")
                .available(true)
                .build();
        Item item1 = Item.builder()
                .id(1L)
                .name("name for item 1")
                .description("description for item 1")
                .owner(user)
                .available(true)
                .build();
        ItemDtoResponse itemDtoResponse = ItemMapper.toItemDtoResponse(item1);

        assertNotNull(itemDtoResponse);
        assertEquals(itemDtoResponse.getId(), 1L);
        assertEquals(itemDtoResponse.getDescription(), itemDtoRequest1.getDescription());
        assertEquals(itemDtoResponse.getName(), itemDtoRequest1.getName());
        assertEquals(itemDtoResponse.getAvailable(), itemDtoRequest1.getAvailable());

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(user.getId(),
                itemDtoRequest1.getId(), itemDtoRequest1));
        assertEquals("Вещь с ID 1 не зарегистрирован!", ex.getMessage());
    }

    @Test
    public void testAddComment__authorNull_throwException() {
        Long authorId = 5L;
        Long itemId = 3L;
        CommentDtoRequest commentDto = new CommentDtoRequest("Test comment");

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> itemService.addComment(itemId, authorId, commentDto));
        assertEquals("Вещь с ID 3 не зарегистрирован!", ex.getMessage());
    }

    @Test
    public void testGetAllItemsUser() {
        Long ownerId = 1L;
        int from = -1;
        int size = 10;

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> itemService.getAllItemsUser(ownerId, from, size));
        assertEquals("Пользователь с ID 1 не зарегистрирован!", ex.getMessage());
    }
}
