package tests;

import com.fasterxml.jackson.annotation.JsonInclude;
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

//5. Частичное обновление бронирования (PATCH /booking/{id})
public class PartialUpdateBookingTest {

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

        //1. Создаем объект Booking с необходимыми данными
        newBooking = new NewBooking();
        newBooking.setFirstname("Sergey");
        newBooking.setLastname("Brother");
        newBooking.setTotalprice(789);
        newBooking.setDepositpaid(false);
        newBooking.setBookingdates(new CheckDate("2024-11-30", "2024-12-02"));
        newBooking.setAdditionalneeds("Dinner");

        //2. Выполняем запрос к эндпоинту /booking через APIClient
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
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    public void partUpdateBooking() throws JsonProcessingException {
        //6. Создаем объект Booking с необходимыми данными
        updateBooking = new NewBooking();
        updateBooking.setFirstname("Severus");
        updateBooking.setLastname("Snape");

        apiClient.createToken("admin", "password123");

        //7. Выполняем запрос к эндпоинту /booking через APIClient
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        String requestBody = objectMapper.writeValueAsString(updateBooking);
        Response response = apiClient.partUpdateBooking(requestBody, bookingId);

        //8. Проверяем, что статус код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //9. Десериализируем тело ответа в объект Booking
        String responseBody = response.asString();
        NewBooking responseUpdateFromBooking = objectMapper.readValue(responseBody,  NewBooking.class);

        //10. Проверяем, что тело ответа содержит объект нового бронирования
        assertThat(responseUpdateFromBooking).isNotNull();
        assertEquals(responseUpdateFromBooking.getFirstname(), updateBooking.getFirstname());
        assertEquals(responseUpdateFromBooking.getLastname(), updateBooking.getLastname());
        assertEquals(responseUpdateFromBooking.getTotalprice(), newBooking.getTotalprice());
        assertEquals(responseUpdateFromBooking.getDepositpaid(), newBooking.getDepositpaid());
        assertEquals(responseUpdateFromBooking.getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(responseUpdateFromBooking.getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
        assertEquals(responseUpdateFromBooking.getAdditionalneeds(), newBooking.getAdditionalneeds());
    }

    @AfterEach
    public void tearDown() {
        //11. Удаляем созданное бронирование
        apiClient.createToken("admin", "password123");
        apiClient.deleteBooking(responceBooking.getBookingid());

        //12. Проверяем, что бронирование успешно удалено
        assertThat(apiClient.getBookingByIdForDeleteItems(String.valueOf(responceBooking.getBookingid())).getStatusCode()).isEqualTo(404);
    }
}
