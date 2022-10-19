package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    private ItemRequestDtoInput itemRequestDtoInput;
    private ItemRequestDtoOutput itemRequestDtoOutput;
    private RequestAndResponseDtoOut requestAndResponseDtoOut;
    private RequestAndResponseDtoOut.ItemDto itemDto;
    @BeforeEach
    void beforeEach() {
        itemDto = new RequestAndResponseDtoOut.ItemDto(1L, "item1", "items", true,1L,1L);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String format = LocalDateTime.now().format(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(format);
        itemRequestDtoInput = new ItemRequestDtoInput("Описание запроса");
        itemRequestDtoOutput = new ItemRequestDtoOutput(1L, "Описание запроса", localDateTime);
        requestAndResponseDtoOut = new RequestAndResponseDtoOut(1L,"Описание запроса", localDateTime, List.of(itemDto));
    }

    @Test
    void testCreateRequest() throws Exception {
        when(itemRequestService.createRequest(anyLong(), any()))
                .thenReturn(itemRequestDtoOutput);
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoOutput.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDtoOutput.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testFindRequestsOfOwner() throws Exception {
        when(itemRequestService.createRequest(1L, itemRequestDtoInput))
                .thenReturn(itemRequestDtoOutput);
        when(itemRequestService.findRequestsOfOwner(anyLong()))
                .thenReturn(List.of(requestAndResponseDtoOut));

        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(requestAndResponseDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestAndResponseDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestAndResponseDtoOut.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    void testFindRequestsOfUser() throws Exception {
        when(itemRequestService.createRequest(1L, itemRequestDtoInput))
                .thenReturn(itemRequestDtoOutput);
        when(itemRequestService.findRequestsOfUser(anyLong(),anyInt(),anyInt()))
                .thenReturn(List.of(requestAndResponseDtoOut));

        mockMvc.perform(get("/requests/all?from=0&size=20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",2L)
                        .param("from","0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(requestAndResponseDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestAndResponseDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestAndResponseDtoOut.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    void testFindRequest() throws Exception {
        when(itemRequestService.createRequest(1L, itemRequestDtoInput))
                .thenReturn(itemRequestDtoOutput);
        when(itemRequestService.findRequest(anyLong(),anyLong()))
                .thenReturn(requestAndResponseDtoOut);

        mockMvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(requestAndResponseDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestAndResponseDtoOut.getDescription())))
                .andExpect(jsonPath("$.created", is(requestAndResponseDtoOut.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }
}
