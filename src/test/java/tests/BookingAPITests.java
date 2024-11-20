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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingAPITests {
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
        Response response = apiClient.getBookingById("/10");

        //Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в объект BookingId
        String responseBody = response.getBody().asString();
        BookingId bookingIds = objectMapper.readValue(responseBody, new TypeReference<BookingId>() {});

        //Проверяем, что тело ответа содержит объекты BookingId
        assertThat(bookingIds).isNotNull(); //Проверяем, что список не пуст

        // Проверка полей полученного ответа
        assertEquals("Sally", bookingIds.getFirstname(), "firstname не совпадает");
        assertEquals("Brown", bookingIds.getLastname(), "lastname не совпадает");
        assertEquals(364, bookingIds.getTotalprice(), "totalprice не совпадает");
        assertEquals(false, bookingIds.isDepositpaid(), "depositpaid не совпадает");
        assertEquals("2024-06-05", bookingIds.getBookingdates().getCheckin(), "checkin не совпадает");
        assertEquals("2024-09-26", bookingIds.getBookingdates().getCheckout(), "checkout не совпадает");
        assertEquals("Breakfast", bookingIds.getAdditionalneeds(), "additionalneeds не совпадает");
    }

    @Test
    public void testDeleteBooking() throws JsonProcessingException {
        //Шаг 1. Получаем список всех id бронирований(GetBookingIds)
        //1.1) Выполняем запрос к эндпоинту /booking через APIClient
        Response responseGet = apiClient.getBooking();

        //1.2) Проверяем, что статус-код ответа равен 200
        assertThat(responseGet.getStatusCode()).isEqualTo(200);

        //1.3) Десериализуем тело ответа в список объектов Booking
        String responseBody = responseGet.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {});

        //1.4) Проверяем, что тело ответа содержит объекты Booking
        assertThat(bookings).isNotEmpty(); //Проверяем, что список не пуст

        //1.5) Проверяем, что каждый объект Booking содержит валидное значение bookingid
        for (Booking booking: bookings) {
            assertThat(booking.getBookingid()).isGreaterThan(0); // bookingid должен быть больше 0
        }
        //1.6) Проверяем, что размер листа bookings не нулевой (> 0)
        assertTrue(!bookings.isEmpty());

        //Шаг 2. Выбираем один id из списка доступных/полученных
        int selectId = 25;
        assertTrue(selectId < bookings.size()); //Проверяем, что выбранный id в доступном диспазоне

        //Шаг 3. Удаляем этот id
        //3.1) Создаем админский токен
        apiClient.createToken("admin", "password123");

        //3.2) Выполняем запрос к эндпоинту /booking через APIClient
        Response responseDelete = apiClient.deleteBooking(selectId);

        //3.3) Проверяем, что статус-код ответа равен 201
        assertThat(responseDelete.getStatusCode()).isEqualTo(201);

        //Шаг 4. Проверяем, что этого id не существует (GetBooking)
        //4.1) Выполняем запрос к эндпоинту /booking через APIClient
        Response responseGetId = apiClient.getBookingByIdForDeleteItems(String.valueOf(selectId));

        //4.2) Проверяем, что статус-код ответа равен 404 (объект удален)
        assertThat(responseGetId.getStatusCode()).isEqualTo(404);
    }
}
