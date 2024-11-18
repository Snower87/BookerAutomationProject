package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import core.models.BookingId;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static core.settings.ApiEndpoint.ID_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HealthCheckTests {
    private APIClient apiClient;
    private ObjectMapper objectMapper;

    // Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    // Тест на метод ping()
    @Test
    public void testPing() {
        // Выполняем GET-запрос на /ping через APIClient
        Response response = apiClient.ping();
        assertThat(response.getStatusCode()).isEqualTo(201);
    }

    @Test
    public void testGetBooking() throws JsonProcessingException {
        //Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.getBooking();

        //Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {});

        //Проверяем, что тело ответа содержит объекты Booking
        assertThat(bookings).isNotEmpty(); //Проверяем, что список не пуст

        //Проверяем, что каждый объект Booking содержит валидное значение bookingid
        for (Booking booking: bookings) {
            assertThat(booking.getBookingid()).isGreaterThan(0); // bookingid должен быть больше 0
        }
    }

    @Test
    public void testGetBookingId10() throws JsonProcessingException {
        //Выполняем запрос к эндпоинту /bookingId через APIClient
        Response response = apiClient.getBookingById(ID_10);

        //Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в объект BookingId
        String responseBody = response.getBody().asString();
        BookingId bookingIds = objectMapper.readValue(responseBody, new TypeReference<BookingId>() {});

        //Проверяем, что тело ответа содержит объекты BookingId
        assertThat(bookingIds).isNotNull(); //Проверяем, что список не пуст

        // Проверка полей полученного ответа
        assertEquals("Mary", bookingIds.getFirstname(), "firstname не совпадает");
        assertEquals("Jackson", bookingIds.getLastname(), "lastname не совпадает");
        assertEquals(888, bookingIds.getTotalprice(), "totalprice не совпадает");
        assertEquals(false, bookingIds.isDepositpaid(), "depositpaid не совпадает");
        assertEquals("2022-10-18", bookingIds.getBookingdates().getCheckin(), "checkin не совпадает");
        assertEquals("2023-12-20", bookingIds.getBookingdates().getCheckout(), "checkout не совпадает");
        assertEquals("Breakfast", bookingIds.getAdditionalneeds(), "additionalneeds не совпадает");
    }
}
