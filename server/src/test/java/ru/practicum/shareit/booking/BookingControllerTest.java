package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.handler.exception.ValidationException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    private BookingDto bookingDto;
    private BookingDto bookingDto1;
    private BookingDtoInput bookingDtoInput;
    private BookingDto.ItemDto itemDto1;
    private BookingDto.UserDto booker;

    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    String format = LocalDateTime.now().format(formatter);
    LocalDateTime localDateTime = LocalDateTime.parse(format).withNano(0);

    @BeforeEach
    void beforeEach() {
        booker = new BookingDto.UserDto(1L, "user1", "user1@mail.ru");
        itemDto1 = new BookingDto.ItemDto(1L, "Дрель", "Простая дрель", true);
        bookingDto = new BookingDto(1L, localDateTime, localDateTime.plusDays(1), itemDto1, booker, StatusBooking.WAITING);
        bookingDto1 = new BookingDto(1L, localDateTime, localDateTime.plusDays(1), itemDto1, booker, StatusBooking.APPROVED);
        bookingDtoInput = new BookingDtoInput(1L, localDateTime, localDateTime.plusDays(1));
    }

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    void testSetApproved() throws Exception {
        when(bookingService.setApproved(anyLong(), anyLong(), anyString()))
                .thenReturn(bookingDto1);
        mockMvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingDto1.getStatus()))));
    }

    @Test
    void testFindBookingById() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto1);
        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingDto1.getStatus()))));
    }

    @Test
    void testGetBookingsOfUser() throws Exception {
        when(bookingService.getBookingsOfUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1));
        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto1.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingDto1.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(bookingDto1.getStatus()))));
    }

    @Test
    void testGetBookingsOfUser400() throws Exception {
        when(bookingService.getBookingsOfUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1));
        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "REQ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Unknown state: UNSUPPORTED_STATUS",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void testGetBookingsAllItems() throws Exception {
        when(bookingService.getBookingsAllItems(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1));
        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto1.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingDto1.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(bookingDto1.getStatus()))));
    }

    @Test
    void testGetBookingsAllItems400() throws Exception {
        when(bookingService.getBookingsAllItems(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1));
        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "REQ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Unknown state: UNSUPPORTED_STATUS",
                        result.getResolvedException().getMessage()));
    }
}
