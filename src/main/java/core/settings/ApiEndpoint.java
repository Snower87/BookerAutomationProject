package core.settings;

public enum ApiEndpoint {
    PING("/ping"),
    BOOKING("/booking"),
    ID_10("/10");

    private final String path;

    ApiEndpoint(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
