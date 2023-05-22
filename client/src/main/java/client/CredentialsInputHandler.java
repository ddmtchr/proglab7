package client;

import java.io.Console;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CredentialsInputHandler {
    private final Scanner scanner;
    private final Console terminal;
    private int action;

    public CredentialsInputHandler() {
        scanner = new Scanner(System.in);
        terminal = System.console();
        action = -1;
    }

    public List<String> getCredentials() {
        while (!(action == 1 || action == 0)) action = askForAction();
        String userLogin;
        String userPassword;
        do {
            userLogin = askForLogin(action);
            if (userLogin.length() < 3) System.out.println("Имя пользователя слишком короткое!");
        } while (userLogin.length() < 3);
        do {
            userPassword = askForPassword(action);
            if (userPassword.length() < 6 && action == 0) System.out.println("Пароль слишком короткий!");
        } while (userPassword.length() < 6 && action == 0);
        List<String> credentials = new ArrayList<>();
        credentials.add(0, userLogin);
        credentials.add(1, userPassword);
        return credentials;
    }

    private int askForAction() throws NoSuchElementException {
        String action = scanner.nextLine().trim();
        if (action.equals("0")) {
            return 0;
        }
        else if (action.equals("1")) {
            return 1;
        }
        else {
            System.out.println("Такого варианта нет, повторите ввод: ");
            return -1;
        }
    }

    private String askForLogin(int action) {
        String message = (action == 0 ? "Введите имя пользователя (минимум 3 символа): " : "Введите имя пользователя: ");
        if (terminal != null) return String.valueOf(terminal.readLine(message));
        else {
            System.out.println(message);
            return scanner.nextLine();
        }
    }

    private String askForPassword(int action) {
        String message = (action == 0 ? "Введите пароль (минимум 6 символов): " : "Введите пароль: ");
        if (terminal != null) return String.valueOf(terminal.readPassword(message));
        else {
            System.out.println(message);
            return scanner.nextLine();
        }
    }

    public String generateSalt() {
        final int saltLength = 10;
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[saltLength];
        secureRandom.nextBytes(salt);
        StringBuilder hexSalt = new StringBuilder();
        for (byte b: salt) {
            hexSalt.append(String.format("%02x", b));
        }
        return hexSalt.toString();
    }

    public String encryptPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD2");
        password += salt;
        byte[] passwordBytes = password.getBytes();
        byte[] digest = md.digest(passwordBytes);
        StringBuilder hexPassword = new StringBuilder();
        for (byte b: digest) {
            hexPassword.append(String.format("%02x", b));
        }
        return hexPassword.toString();
    }

    public int getAction() {
        return action;
    }
}
