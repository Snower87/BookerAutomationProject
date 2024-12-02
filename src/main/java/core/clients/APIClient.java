package core.clients;

import core.settings.ApiEndpoint;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIClient {
    private final String baseUrl;
    private String token;

    public APIClient() {
        this.baseUrl = determineBaseUrl();
    }

    private String determineBaseUrl() {
        String environment = System.getProperty("env", "test");
        String configFileName = "application-" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found:" + configFileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties.getProperty("baseUrl");
    }

    //Настройка базовых параметров HTTP-запросов
    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(baseUrl)  // Устанавливаем базовый URL
                .header("Content-Type","application/json")  // Заголовок, указывающий формат данных
                .header("Accept", "application/json") // Заголовок, указывающий на принимаемый формат
                .filters(addAuthTokenFilter()); // Фильтр для добавления токена
    }

    //Метод для получения токена
    public void createToken(String username, String password) {
        // Тело запроса для формирования токена
        String requestBody = String.format("{ \"username\": \"%s\", \"password\": \"%s\" }", username, password);

        // Отправка POST-запроса на эндпоинт для аутентификации и получение токена
        Response response = getRequestSpec()
                .body(requestBody) // Устанавливаем тело запроса
                .when()
                .post(ApiEndpoint.AUTH.getPath()) // POST-запрос на эндпоинт аутентификации
                .then()
                .statusCode(200) // Проверяем, что статус ответа 200 (ОК)
                .extract()
                .response();

        // Извлечение токена из ответа
        token = response.jsonPath().getString("token");
    }

    private Filter addAuthTokenFilter() {
        return (FilterableRequestSpecification requestSpec,
                FilterableResponseSpecification responseSpec, FilterContext ctx) -> {
            if (token != null) {
                requestSpec.header("Cookie", "token=" + token);
            }
            return ctx.next(requestSpec, responseSpec); // Продолжает выполнение запроса
        };
    }

    //GET-запрос на эндпоинт /ping
    public Response ping() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoint.PING.getPath()) // используем ENUM для эндпоинта /ping
                .then()
                .statusCode(201) //ожидаемый статус-код 201 Created
                .extract()
                .response();
    }

    //GET-запрос на эндпоинт /booking
    public Response getBooking() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoint.BOOKING.getPath()) // используем ENUM для эндпоинта /getBooking
                .then()
                .statusCode(200) //ожидаемый статус-код 200 OK
                .extract()
                .response();
    }

    //GET-запрос на эндпоинт /bookingId
    public Response getBookingById(String ids) {
        return getRequestSpec()
                .when()
                .get(ApiEndpoint.BOOKING.getPath() + ids) // используем ids для эндпоинта /getBookingIds
                .then()
                .statusCode(200) //ожидаемый статус-код 200 OK
                .extract()
                .response();
    }

    //GET-запрос на эндпоинт /bookingId
    public Response getBookingByIdForDeleteItems(String ids) {
        return getRequestSpec()
                .when()
                .get(ApiEndpoint.BOOKING.getPath() + ids) // используем ids для эндпоинта /getBookingIds
                .then()
                .statusCode(404) //ожидаемый статус-код 404 OBJECT NOT FOUND
                .extract()
                .response();
    }

    //DELETE-запрос на эндпоинт /booking
    public Response deleteBooking(int bookingId) {
        return getRequestSpec()
                .pathParam("id", bookingId)
                .when()
                .delete(ApiEndpoint.BOOKING.getPath() + "/{id}")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .response();
    }

    //POST-запрос на создание бронирования
    public Response createBooking(String newBooking) {
        return getRequestSpec()
                .body(newBooking)
                .log().all()
                .when()
                .post(ApiEndpoint.BOOKING.getPath())
                .then()
                .log().all()
                .extract()
                .response();
    }

    //PUT-запрос на обновлении данных бронирования
    public Response updateBooking(String newBooking, int bookingId) {
        return getRequestSpec()
                .body(newBooking)
                .pathParam("id", bookingId)
                .log().all()
                .when()
                .put(ApiEndpoint.BOOKING.getPath() + "/{id}")
                .then()
                .log().all()
                .extract()
                .response();
    }

    //PATCH-запрос на частичное обновлении данных
    public Response partUpdateBooking(String newBooking, int bookingId) {
        return getRequestSpec()
                .body(newBooking)
                .pathParam("id", bookingId)
                .log().all()
                .when()
                .patch(ApiEndpoint.BOOKING.getPath() + "/{id}")
                .then()
                .log().all()
                .extract()
                .response();
    }

    //GET-запрос на эндпоинт /booking с параметрами
    public Response filterBookingByParam(String filterByParam) {
        return getRequestSpec()
                .when()
                .get(ApiEndpoint.BOOKING.getPath() + "?" + filterByParam) // используем ENUM для эндпоинта /getBooking
                .then()
                .log().all()
                .extract()
                .response();
    }
}
