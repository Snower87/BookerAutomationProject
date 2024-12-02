package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

//6. Получение бронирований с фильтрацией по параметрам (GET /booking):
// - firstName,
// - lastName,
// - checkin, checkout
public class FilterBookingByParamTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private NewBooking newBooking;
    private ResponceBooking responceBooking;
    private List<NewBooking> newBookingList = new ArrayList<>();
    private List<ResponceBooking> responceBookingList = new ArrayList<>();

    //Инициализация API клиента и создание объекта Booking перед каждым тестом
    @BeforeEach
    public void setup() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");

        String[] listFirstName = new String[] {"Jim", "Jim",    "Jim",      "Sally",    "Joker", "Sally"};
        String[] listLastName = new String[] {"Brown", "Brown", "Rebekk",   "Rodriger", "Hex",  "Pencilvania"};
        String[] listCheckin  = new String[] {"2014-02-02", "2014-02-02", "2015-12-10", "2017-05-05", "2000-01-01", "2022-01-01"};
        String[] listCheckout = new String[] {"2015-01-01", "2015-01-01", "2015-12-12", "2017-05-08", "2001-01-01", "2023-01-01"};

        for (int i = 0; i < 6; i ++) {
            //1. Создаем объект Booking с необходимыми данными
            newBooking = new NewBooking();
            newBooking.setFirstname(listFirstName[i]);
            newBooking.setLastname(listLastName[i]);
            newBooking.setTotalprice(111 + i);
            newBooking.setDepositpaid(true);
            newBooking.setBookingdates(new CheckDate(listCheckin[i], listCheckout[i]));
            newBooking.setAdditionalneeds("Breakfast");

            //2. Добавляем объект в список для запроса
            newBookingList.add(newBooking);
        }

        for (int i = 0; i < 6; i ++) {
            //3. Выполняем запрос к эндпоинту /booking через APIClient
            objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(newBookingList.get(i));
            Response response = apiClient.createBooking(requestBody);

            //4. Проверяем, что статус код ответа равен 200
            assertThat(response.getStatusCode()).isEqualTo(200);

            //5. Десериализируем тело ответа в объект Booking
            String responseBody = response.asString();
            responceBooking = objectMapper.readValue(responseBody, ResponceBooking.class);

            //6. Проверяем, что тело ответа содержит объект нового бронирования
            assertThat(responceBooking).isNotNull();
            assertEquals(responceBooking.getBooking().getFirstname(), newBookingList.get(i).getFirstname());
            assertEquals(responceBooking.getBooking().getLastname(), newBookingList.get(i).getLastname());
            assertEquals(responceBooking.getBooking().getTotalprice(), newBookingList.get(i).getTotalprice());
            assertEquals(responceBooking.getBooking().getDepositpaid(), newBookingList.get(i).getDepositpaid());
            assertEquals(responceBooking.getBooking().getBookingdates().getCheckin(), newBookingList.get(i).getBookingdates().getCheckin());
            assertEquals(responceBooking.getBooking().getBookingdates().getCheckout(), newBookingList.get(i).getBookingdates().getCheckout());
            assertEquals(responceBooking.getBooking().getAdditionalneeds(), newBookingList.get(i).getAdditionalneeds());

            responceBookingList.add(responceBooking);
        }
    }

    @Test
    public void filterByFirstName() throws JsonProcessingException {
        //7. Создали все необходимые данные, а теперь выполняем фильтрацию:
        // - по firstname:
        //Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.filterBookingByParam("firstname=Jim");

        //8. Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //9. Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        ArrayList<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<ArrayList<Booking>>() {});

        //10. Вношу все полученные id  после десеарилизации в список для дальнейшего использования (сверки)
        LinkedList<Integer> arrayFilteredIds = new LinkedList<>();
        for (Booking booking: bookings) {
            arrayFilteredIds.add(booking.getBookingid());
        }

        int expectedFindJim = 3;
        int findJimInResponse = 0;

        //11. Проверяю, что все созданные id объектов есть в отфильтрованном списке по полю firstname
        for (ResponceBooking responceBooking : responceBookingList) {
            if (responceBooking.getBooking().getFirstname().equals("Jim")) {
                int findId = responceBooking.getBookingid();
                int indexFilterElement = arrayFilteredIds.indexOf(findId);

                if (indexFilterElement != -1) findJimInResponse++;

                //12. Сравниваем id объектов: созданных и после фильтрации
                assertEquals(responceBooking.getBookingid(), arrayFilteredIds.get(indexFilterElement));
            }
        }

        //13. Сравниваем кол-во найденных объектов "Jim"
        assertEquals(expectedFindJim, findJimInResponse);
    }

    @Test
    public void filterByLastName() throws JsonProcessingException {
        //7. Создали все необходимые данные, а теперь выполняем фильтрацию:
        // - по lastname:
        //Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.filterBookingByParam("lastname=Brown");

        //8. Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //9. Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        ArrayList<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<ArrayList<Booking>>() {});

        //10. Вношу все полученные id  после десеарилизации в список для дальнейшего использования (сверки)
        LinkedList<Integer> arrayFilteredIds = new LinkedList<>();
        for (Booking booking: bookings) {
            arrayFilteredIds.add(booking.getBookingid());
        }

        int expectedFindBrown = 2;
        int findBrownInResponse = 0;

        //11. Проверяю, что все созданные id объектов есть в отфильтрованном списке по полю lastname
        for (ResponceBooking responceBooking : responceBookingList) {
            if (responceBooking.getBooking().getLastname().equals("Brown")) {
                int findId = responceBooking.getBookingid();
                int indexFilterElement = arrayFilteredIds.indexOf(findId);

                if (indexFilterElement != -1) findBrownInResponse++;

                //12. Сравниваем id объектов: созданных и после фильтрации
                assertEquals(responceBooking.getBookingid(), arrayFilteredIds.get(indexFilterElement));
            }
        }

        //13. Сравниваем кол-во найденных объектов "Brown"
        assertEquals(expectedFindBrown, findBrownInResponse);
    }

    @Test
    public void filterByCheckDates() throws JsonProcessingException {
        //7. Создали все необходимые данные, а теперь выполняем фильтрацию:
        // - по checkin + checkout:
        //Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.filterBookingByParam("checkin=2000-01-01&checkout=2001-01-01");

        //8. Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //9. Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        ArrayList<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<ArrayList<Booking>>() {});

        //10. Вношу все полученные id  после десеарилизации в список для дальнейшего использования (сверки)
        LinkedList<Integer> arrayFilteredIds = new LinkedList<>();
        for (Booking booking: bookings) { //!!! bookings: [] (size = 0)
            arrayFilteredIds.add(booking.getBookingid());
        }

        int expectedFindCheckDates = 1;
        int findCheckDatesInResponse = 0;

        //11. Проверяю, что все созданные id объектов есть в отфильтрованном списке по полю firstname
        for (ResponceBooking responceBooking : responceBookingList) { //!!! responceBookingList: 4642, 4655, 4663, 4669, 4674, 4681 (size = 6)
            if (responceBooking.getBooking().getBookingdates().getCheckin().equals("2000-01-01") &&
                    responceBooking.getBooking().getBookingdates().getCheckout().equals("2001-01-01")) {
                int findId = responceBooking.getBookingid();
                int indexFilterElement = arrayFilteredIds.indexOf(findId);

                if (indexFilterElement != -1) findCheckDatesInResponse++;

                //12. Сравниваем id объектов: созданных и после фильтрации
                assertEquals(responceBooking.getBookingid(), arrayFilteredIds.get(indexFilterElement));
            }
        }

        //13. Сравниваем кол-во найденных объектов CheckDates
        assertEquals(expectedFindCheckDates, findCheckDatesInResponse);
    }

    @AfterEach
    public void tearDown() {
        for (int i = 0; i < 6; i ++) {
            //14. Удаляем созданное бронирование
            apiClient.createToken("admin", "password123");
            apiClient.deleteBooking(responceBookingList.get(i).getBookingid());

            //15. Проверяем, что бронирование успешно удалено
            assertThat(apiClient.getBookingByIdForDeleteItems(String.valueOf(responceBookingList.get(i).getBookingid())).getStatusCode()).isEqualTo(404);
        }
    }
}
