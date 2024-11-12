package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.services.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestResponseDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST запрос на создание запроса вещи");
        return service.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET запрос на получение всех созданных запросов вещей пользователя с ID {}", userId);
        return service.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("GET запрос на получение всех запросов созданных другими пользователями");
        return service.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequest(@PathVariable long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getItemRequest(requestId, userId);
    }
}