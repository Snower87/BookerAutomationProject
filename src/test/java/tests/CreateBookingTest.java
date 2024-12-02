package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.CheckDate;
import core.models.NewBooking;
import core.models.ResponceBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//1. Создание бронирования (POST /booking)
public class CreateBookingTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private NewBooking newBooking;
    private ResponceBooking responceBooking;

    //Инициализация API клиента и создание объекта Booking перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        //1. Создаем объект Booking со всеми необходимыми данными
        newBooking = new NewBooking();
        newBooking.setFirstname("Jim");
        newBooking.setLastname("Brown");
        newBooking.setTotalprice(111);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new CheckDate("2018-01-01", "2019-01-01"));
        newBooking.setAdditionalneeds("Breakfast");
    }

    @Test
    public void createBooking() throws JsonProcessingException {
        //2. Выполняем запрос к эндпоинту /booking через APIClient
        objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        //3. Проверяем, что статус код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //4. Десериализируем тело ответа в объект Booking
        String responseBody = response.asString();
        responceBooking = objectMapper.readValue(responseBody, ResponceBooking.class);

        //5. Проверяем, что тело ответа содержит объект нового бронирования и bookingid
        assertThat(responceBooking).isNotNull();
        assertEquals(responceBooking.getBooking().getFirstname(), newBooking.getFirstname());
        assertEquals(responceBooking.getBooking().getLastname(), newBooking.getLastname());
        assertEquals(responceBooking.getBooking().getTotalprice(), newBooking.getTotalprice());
        assertEquals(responceBooking.getBooking().getDepositpaid(), newBooking.getDepositpaid());
        assertEquals(responceBooking.getBooking().getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(responceBooking.getBooking().getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
        assertEquals(responceBooking.getBooking().getAdditionalneeds(), newBooking.getAdditionalneeds());
        assertTrue(responceBooking.getBookingid() > 0);
    }

    @AfterEach
    public void tearDown() {
        //6. Удаляем созданное бронирование
        apiClient.createToken("admin", "password123");
        apiClient.deleteBooking(responceBooking.getBookingid());

        //7. Проверяем, что бронирование успешно удалено
        assertThat(apiClient.getBookingByIdForDeleteItems(String.valueOf(responceBooking.getBookingid())).getStatusCode()).isEqualTo(404);
    }
}
