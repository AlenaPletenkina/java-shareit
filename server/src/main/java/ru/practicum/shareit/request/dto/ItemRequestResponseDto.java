package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForItemRequestResponseDto;
import ru.practicum.shareit.user.dto.UserForItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private UserForItemRequestDto requester;
    private LocalDateTime created; //дата и время создания запроса
    List<ItemForItemRequestResponseDto> items;
}
