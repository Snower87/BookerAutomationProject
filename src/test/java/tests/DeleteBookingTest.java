package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteBookingTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private NewBooking newBooking;
    private ResponceBooking responceBooking;

    //Инициализация API клиента и создание объекта Booking перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");
    }

    @Test
    public void deleteBooking() throws JsonProcessingException {
        //1. Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.getBooking();

        //2. Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //3. Десериализируем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {});

        //4. Проверяем, что тело ответа содержит объекты Booking
        assertThat(bookings).isNotEmpty(); //Проверяем, что список не пуст

        //5. Получаем первый в списке id и удаляем его
        int idForDelete = bookings.get(0).getBookingid();
        apiClient.deleteBooking(idForDelete);

        //6. Проверяем, что id удален
        assertThat(apiClient.getBookingByIdForDeleteItems(String.valueOf(idForDelete)).getStatusCode()).isEqualTo(404);
    }
}
