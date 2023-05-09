package validation;

public class IdValidator {
    public static boolean isValid(long id) {
        return id > 0;
    }
}
