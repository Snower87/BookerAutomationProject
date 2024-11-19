package core.settings;

public enum ApiEndpoint {
    PING("/ping"),
    BOOKING("/booking"),
    AUTH("/auth");

    private final String path;

    ApiEndpoint(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
