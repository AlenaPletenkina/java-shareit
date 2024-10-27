package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.services.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    static final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(userHeader) long userId, @Valid @RequestBody ItemDto item) {
        log.info("Получил POST запрос на создание вещи");
        return service.addItem(userId, item);
    }

    @PostMapping("/{item-id}/comment")
    public CommentDtoResponse addComment(@PathVariable("item-id") long itemId, @RequestHeader(userHeader) long userId,
                                         @Valid @RequestBody CommentDtoRequest commentDtoRequest) {
        log.info("POST запрос на создание вещи");
        return service.addComment(itemId, userId, commentDtoRequest);
    }

    @PatchMapping("/{item-id}")
    public ItemDto updateItem(@RequestHeader(userHeader) long userId, @PathVariable("item-id") long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Получил PATCH запрос на обновление вещи");
        return service.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{item-id}")
    public ItemForBookingDto getItem(@RequestHeader(userHeader) Long ownerId,
                                     @PathVariable("item-id") Long itemId) {
        log.info("GET запрос на получение вещи с ID: {}", itemId);
        return service.getItemDto(ownerId, itemId);
    }

    @GetMapping
    public List<ItemForBookingDto> getAllItemsUser(@RequestHeader(userHeader) long userId) {
        log.info("GET запрос на получение всех вещей пользователя с ID: {}", userId);
        return service.getAllItemsUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchOfText(@RequestParam String text) {
        log.info("Получил GET запрос на получение всех вещей с текстом: {}", text);
        return service.getSearchOfText(text);
    }
}
