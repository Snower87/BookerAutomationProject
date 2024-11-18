package core.clients;

import core.settings.ApiEndpoint;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIClient {
    private final String baseUrl;

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
                .baseUri(baseUrl)
                .header("Content-Type","application/json")
                .header("Accept", "application/json");
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
                .get(ApiEndpoint.BOOKING.getPath()) // используем ENUM для эндпоинта /ping
                .then()
                .statusCode(200) //ожидаемый статус-код 200 OK
                .extract()
                .response();
    }
}
