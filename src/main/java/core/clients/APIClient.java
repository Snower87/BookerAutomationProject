package core.clients;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties.getProperty("baseUrl");
    }
}
