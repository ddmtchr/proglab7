package utility;

public class ResponseBuilder {
    private static final StringBuilder response = new StringBuilder();

    public static void append(String s) {
        response.append(s);
    }

    public static void appendln(String s) {
        response.append(s).append("\n");
    }

    public static String getAndClear() {
        String responseToGet = response.toString();
        response.setLength(0);
        return responseToGet;
    }
}
