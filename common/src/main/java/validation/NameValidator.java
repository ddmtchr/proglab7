package validation;

public class NameValidator {
    public static boolean isValid(String name) {
        return !(name.isBlank() || name.isEmpty());
    }
}
