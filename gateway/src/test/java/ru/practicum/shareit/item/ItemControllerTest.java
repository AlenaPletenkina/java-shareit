package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = ShareItGateway.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemClient itemClient;

    @Test
    void testAddItemWrong() throws Exception {
        int userId = 1;
        ItemDtoRequest itemDto = getItemDto("");
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemClient, Mockito.never()).postItem(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    @Test
    void getItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemClient).getItem(itemId, userId);
    }

    @Test
    void testItemExceptionStatus500() throws Exception {
        long itemId = 69;
        long userId = 1;
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());

        Mockito.verify(itemClient, Mockito.never()).getItem(itemId, userId);
    }

    @Test
    void testItemsByOwner() throws Exception {
        long userId = 1;
        int from = 1;
        int size = 10;
        mockMvc.perform(MockMvcRequestBuilders.get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemClient).getAllItemsUser(userId, 1, 10);
    }

    @Test
    public void testSearchItemsByTextNullSizeTest() throws Exception {
        String text = "one item";
        Integer from = 1;
        Integer size = 0;

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", text)
                        .param("from", "1")
                        .param("size", "0")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testItemsByOwnerWrongPage() throws Exception {
        long userId = 1;
        int from = -1;
        int size = -10;
        mockMvc.perform(MockMvcRequestBuilders.get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verify(itemClient, Mockito.never()).getAllItemsUser(userId, 1, 10);
    }

    ItemDtoRequest getItemDto(String name) {
        return new ItemDtoRequest(
                1L,
                name,
                "Описание",
                false,
                null
        );
    }
}
