package ru.practicum.shareit.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    private ItemDto itemDto1;

    private ItemDtoUpdate itemDtoUpdate1;

    private CommentDto commentDto1;

    private ItemInfoDto itemInfoDto1;

    @BeforeEach
    void beforeEach() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String format = LocalDateTime.now().format(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(format).withNano(0);
        itemDto1 = new ItemDto(1L, "Дрель", "Простая дрель", true, null);
        itemDtoUpdate1 = new ItemDtoUpdate(1L, "Дрель+", "Аккумуляторная дрель", false);
        itemInfoDto1 = new ItemInfoDto(1L, "Отвертка", "Электрическа отвертка", false,
                null, null, List.of());
        commentDto1 = new CommentDto(1L, "Add comment from user1", "user1", localDateTime);
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.createItem(anyLong(), any()))
                .thenReturn(itemDto1);
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())));
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto(
                itemDtoUpdate1.getId(),
                itemDtoUpdate1.getName(),
                itemDtoUpdate1.getDescription(),
                itemDtoUpdate1.getAvailable(),
                null);
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoUpdate1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("itemId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdate1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdate1.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdate1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdate1.getAvailable())));
    }

    @Test
    void testFindItemById() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemInfoDto1);
        mockMvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("itemId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemInfoDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfoDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemInfoDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemInfoDto1.getAvailable())));
    }

    @Test
    void testFindItemsOfUser() throws Exception {
        when(itemService.findItemsOfUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemInfoDto1));
        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("itemId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemInfoDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemInfoDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemInfoDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemInfoDto1.getAvailable())));
    }

    @Test
    void testSearchItem() throws Exception {
        when(itemService.searchItem(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto1));
        mockMvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "ДреЛь")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())));
    }

    @Test
    void testCreateComment() throws Exception {
        when(itemService.createComment(anyLong(), any(), anyLong()))
                .thenReturn(commentDto1);
        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(new CommentDto(null, "Add comment from user1",
                                null, null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("itemId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto1.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto1.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto1.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto1.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }
}
