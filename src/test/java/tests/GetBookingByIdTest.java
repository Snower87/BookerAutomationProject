package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

//3. Получение бронирования по ID (GET /booking/{id})
public class GetBookingByIdTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private NewBooking newBooking;
    private ResponceBooking responceBooking;
    private int bookingId;

    //Инициализация API клиента и создание объекта Booking перед каждым тестом
    @BeforeEach
    public void setup() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        //1. Создаем объект Booking с необходимыми данными
        newBooking = new NewBooking();
        newBooking.setFirstname("Sergey");
        newBooking.setLastname("Brother");
        newBooking.setTotalprice(789);
        newBooking.setDepositpaid(false);
        newBooking.setBookingdates(new CheckDate("2024-11-30", "2024-12-02"));
        newBooking.setAdditionalneeds("Dinner");

        //2. Выполняем запрос к эндпоинту /booking через APIClient
        // Создаем бронирование через POST-запрос
        objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        //3. Проверяем, что статус код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //4. Десериализируем тело ответа в объект Booking
        String responseBody = response.asString();
        responceBooking = objectMapper.readValue(responseBody, ResponceBooking.class);

        //5. Получаем созданный {id} бронирования
        bookingId = responceBooking.getBookingid();
    }

    @Test
    public void getBookingById() throws JsonProcessingException {
        //6. Выполняем запрос к эндпоинту /bookingId через APIClient
        Response response = apiClient.getBookingById("/" + Integer.toString(bookingId));

        //7. Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //8. Десериализируем тело ответа в объект CreateBooking
        String responseBody = response.asString();
        NewBooking responseBookingId = objectMapper.readValue(responseBody, NewBooking.class);

        //9. Проверяем, что тело ответа содержит объект нового бронирования
        assertThat(responseBookingId).isNotNull();
        assertEquals(responseBookingId.getFirstname(), newBooking.getFirstname());
        assertEquals(responseBookingId.getLastname(), newBooking.getLastname());
        assertEquals(responseBookingId.getTotalprice(), newBooking.getTotalprice());
        assertEquals(responseBookingId.getDepositpaid(), newBooking.getDepositpaid());
        assertEquals(responseBookingId.getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(responseBookingId.getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
        assertEquals(responseBookingId.getAdditionalneeds(), newBooking.getAdditionalneeds());
    }

    @AfterEach
    public void tearDown() {
        //10. Удаляем созданное бронирование
        apiClient.createToken("admin", "password123");
        apiClient.deleteBooking(responceBooking.getBookingid());

        //11. Проверяем, что бронирование успешно удалено
        assertThat(apiClient.getBookingByIdForDeleteItems(String.valueOf(responceBooking.getBookingid())).getStatusCode()).isEqualTo(404);
    }
}
