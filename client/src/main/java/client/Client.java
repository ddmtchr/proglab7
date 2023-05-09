package client;

import input.CommandInputReceiver;
import utility.Request;
import utility.RequestType;
import utility.Response;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Client {
    private final String hostName;
    private final int port;
    private final Scanner scanner = new Scanner(System.in);
    private CommandInputReceiver cir;
    private ConnectionProvider connectionProvider;
    private CredentialsInputHandler credentialsInputHandler;
    private static final String[] greetings = new String[]{
            "Как ваше ничего, %s? ",
            "Добрый вечер, %s. ",
            "Guten Tag, %s! ",
            "Konnichiwa, %s! ",
            "Храни Вас Господь, %s. "
    };

    public Client(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    private void greet(String username) {
        Random generator = new Random();
        int greetingNumber = generator.nextInt(greetings.length);
        System.out.println(String.format(greetings[greetingNumber], username) +  "Введите help для справки.");
    }

    public void run() {
        try {
            connectionProvider = new ConnectionProvider(hostName, port);
            credentialsInputHandler = new CredentialsInputHandler();
            Session session;

            System.out.println("Введите 0 для регистрации, 1 для авторизации: ");
            List<String> credentials = auth();

            session = new Session(credentials.get(0), credentials.get(1));
            greet(session.getLogin());
            cir = new CommandInputReceiver(scanner, credentials.get(0));

//            int processCode = 0;
            do {
                try {
                    Request request = cir.createRequest(connectionProvider, session);
                    if (request != null) {
                        connectionProvider.send(request);

                        Response response = connectionProvider.receive();
                        if (response == null) throw new SocketTimeoutException();

                        System.out.println(response.getBody());
//                        processCode = response.getExecCode();
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Приносим искренние извинения, сервер скоропостижно скончался");
                }
            } while (true);

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchElementException e) {
            System.out.println("Ввод завершен пользователем");
            System.exit(0);
        }
    }

    private List<String> auth() throws NoSuchAlgorithmException {
        boolean doneAuthentication = false;
        List<String> credentials = new ArrayList<>();
        do {
            try {
                credentials = credentialsInputHandler.getCredentials();
                int action = credentialsInputHandler.getAction();
                String userLogin = credentials.get(0);
                String userPassword = credentials.get(1);

                if (action == 0) {
                    String userSalt = credentialsInputHandler.generateSalt();
                    String encryptedPassword = credentialsInputHandler.encryptPassword(userPassword, userSalt);
                    Request registerRequest = new Request(null, null, null,
                            userLogin, encryptedPassword, userSalt, RequestType.REGISTER);
                    connectionProvider.send(registerRequest);
                    Response registerResponse = connectionProvider.receive();
                    System.out.println(registerResponse.getBody());
                    if (registerResponse == null)
                        throw new SocketTimeoutException();
                    if (registerResponse.getExecCode() == 0) {
                        doneAuthentication = true;
                        credentials.set(1, encryptedPassword);
                    }

                } else if (action == 1) {
                    Request saltRequest = new Request(null, null, null,
                            userLogin, null, null, RequestType.GET_SALT);
                    connectionProvider.send(saltRequest);
                    Response saltResponse = connectionProvider.receive();
                    if (saltResponse == null)
                        throw new SocketTimeoutException();
                    if (saltResponse.getExecCode() == 1) {
                        System.out.println(saltResponse.getBody());
                        continue;
                    }
                    String userSalt = saltResponse.getBody();

                    String encryptedPassword = credentialsInputHandler.encryptPassword(userPassword, userSalt);
                    Request loginRequest = new Request(null, null, null,
                            userLogin, encryptedPassword, null, RequestType.LOGIN);
                    connectionProvider.send(loginRequest);
                    Response loginResponse = connectionProvider.receive();
                    if (loginResponse == null)
                        throw new SocketTimeoutException();
                    if (loginResponse.getExecCode() == 0) {
                        doneAuthentication = true;
                        credentials.set(1, encryptedPassword);
                    }
                    System.out.println(loginResponse.getBody());
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Сервер прилег отдохнуть, попробуйте еще раз");
            }
        } while (!doneAuthentication);
        return credentials;
    }
}
