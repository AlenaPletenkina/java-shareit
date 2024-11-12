package ru.practicum.shareit.item.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.validation.Create;


import java.util.Collections;
//import jakarta.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody @Validated({Create.class}) ItemDtoRequest itemDto) {
        log.info("post item userId={}, itemDto={}", userId, itemDto);
        return itemClient.postItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CommentDtoRequest commentDto) {
        log.info("Post comment userId={}, itemId={}, commentDto={}", userId, itemId, commentDto);
        return itemClient.addComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDtoRequest itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable long itemId) {
        log.info("patch item userId={}, itemId= {}, itemDto={}", userId, itemId, itemDto);
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get itemId={}, ownerId={}", itemId, ownerId);
        return itemClient.getItem(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Get allUserItem userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItemsUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchOfText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam String text,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Get /search text={}, from={}, size={}", text, from, size);
        if (text == null || text.isBlank()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return itemClient.getSearchOfText(userId, text, from, size);
    }
}