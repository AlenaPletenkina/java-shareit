package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    static final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(userHeader) Long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST на добавление запроса userId={}, itemRequestDto={}", userId, itemRequestDto);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(
            @RequestHeader(userHeader) Long userId) {
        log.info("GET на запросы пользователя userId={}", userId);
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader(userHeader) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("GET на все запросы  userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("{request-id}")
    public ResponseEntity<Object> getItemRequest(@PathVariable("request-id") Long requestId,
                                                 @RequestHeader(userHeader) Long userId) {
        log.info("GET userId={}, requestId={}", userId, requestId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }
}
