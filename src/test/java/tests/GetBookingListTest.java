package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.ArrayBooking;
import core.models.Booking;
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

//2. Получение списка всех бронирований (GET /booking)
public class GetBookingListTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private NewBooking newBooking;
    private ResponceBooking responceBooking;
    private ArrayBooking arrayBooking;

    //Инициализация API клиента и создание объекта Booking перед каждым тестом
    @BeforeEach
    public void setup() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        //1. Создаем объект Booking с необходимыми данными
        newBooking = new NewBooking();
        newBooking.setFirstname("Billy");
        newBooking.setLastname("Dark");
        newBooking.setTotalprice(111);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new CheckDate("2018-01-01", "2019-01-01"));
        newBooking.setAdditionalneeds("Breakfast");

        //2. Выполняем запрос к эндпоинту /booking через APIClient
        objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        //3. Проверяем, что статус код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //4. Десериализируем тело ответа в объект Booking
        String responseBody = response.asString();
        responceBooking = objectMapper.readValue(responseBody, ResponceBooking.class);

        //5. Проверяем, что тело ответа содержит объект нового бронирования
        assertThat(responceBooking).isNotNull();
        assertEquals(responceBooking.getBooking().getFirstname(), newBooking.getFirstname());
        assertEquals(responceBooking.getBooking().getLastname(), newBooking.getLastname());
        assertEquals(responceBooking.getBooking().getTotalprice(), newBooking.getTotalprice());
        assertEquals(responceBooking.getBooking().getDepositpaid(), newBooking.getDepositpaid());
        assertEquals(responceBooking.getBooking().getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(responceBooking.getBooking().getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
        assertEquals(responceBooking.getBooking().getAdditionalneeds(), newBooking.getAdditionalneeds());
    }

    @Test
    public void getBookingList() throws JsonProcessingException {
        //6. Выполняем запрос к эндпоинту /booking через APIClient
        objectMapper = new ObjectMapper();
        Response response = apiClient.getBooking();

        //7. Проверяем, что статус код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //8. Десериализируем тело ответа в объект Booking
        String responseBody = response.asString();
        Booking[] arrayBooking = objectMapper.readValue(responseBody, Booking[].class);

        //9. Проверяем, что тело ответа содержит объект нового бронирования
        assertTrue(arrayBooking.length > 0);

        //10. Проверяем, что тело ответа содержит bookingId и они не нулевые
        for (Booking booking: arrayBooking) {
            assertTrue(booking.getBookingid() > 0);
        }
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
