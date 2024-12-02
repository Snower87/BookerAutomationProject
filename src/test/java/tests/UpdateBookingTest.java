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

//4. Обновление бронирования (PUT /booking/{id})
public class UpdateBookingTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private NewBooking newBooking;
    private NewBooking updateBooking;
    private ResponceBooking responceBooking;
    private int bookingId;

    //Инициализация API клиента и создание объекта Booking перед каждым тестом
    @BeforeEach
    public void setup() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");

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
    public void updateBooking() throws JsonProcessingException {
        //6. Создаем объект Booking с необходимыми данными
        updateBooking = new NewBooking();
        updateBooking.setFirstname("Vlad");
        updateBooking.setLastname("Brother");
        updateBooking.setTotalprice(999);
        updateBooking.setDepositpaid(false);
        updateBooking.setBookingdates(new CheckDate("2024-11-30", "2024-12-02"));
        updateBooking.setAdditionalneeds("Dinner_x2");

        //7. Выполняем запрос к эндпоинту /booking через APIClient
        objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(updateBooking);
        Response response = apiClient.updateBooking(requestBody, bookingId);

        //8. Проверяем, что статус код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //9. Десериализируем тело ответа в объект Booking
        String responseBody = response.asString();
        NewBooking responseUpdateFromBooking = objectMapper.readValue(responseBody,  NewBooking.class);

        //10. Проверяем, что тело ответа содержит обновленный объект бронирования
        assertThat(responseUpdateFromBooking).isNotNull();
        assertEquals(responseUpdateFromBooking.getFirstname(), updateBooking.getFirstname());
        assertEquals(responseUpdateFromBooking.getLastname(), updateBooking.getLastname());
        assertEquals(responseUpdateFromBooking.getTotalprice(), updateBooking.getTotalprice());
        assertEquals(responseUpdateFromBooking.getDepositpaid(), updateBooking.getDepositpaid());
        assertEquals(responseUpdateFromBooking.getBookingdates().getCheckin(), updateBooking.getBookingdates().getCheckin());
        assertEquals(responseUpdateFromBooking.getBookingdates().getCheckout(), updateBooking.getBookingdates().getCheckout());
        assertEquals(responseUpdateFromBooking.getAdditionalneeds(), updateBooking.getAdditionalneeds());
    }

    @AfterEach
    public void tearDown() {
        //11. Удаляем созданное бронирование
        apiClient.deleteBooking(responceBooking.getBookingid());

        //12. Проверяем, что бронирование успешно удалено
        assertThat(apiClient.getBookingByIdForDeleteItems(String.valueOf(responceBooking.getBookingid())).getStatusCode()).isEqualTo(404);
    }
}
