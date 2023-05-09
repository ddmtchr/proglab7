package processing;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerCommandExecutor {
    private static final Logger logger = Logger.getLogger(ServerCommandExecutor.class.getName());

    public void execute(String[] serverCommand) {
        if (serverCommand[0].equals("exit")) {
            if (serverCommand[1].equals(" ")) {
                logger.log(Level.INFO, "Выход с сервера");
                System.out.println("Роняем сервер...");
                System.exit(0);
            } else {
                System.out.println("Использование: exit");
            }
        } else {
            System.out.println("Неизвестная команда " + serverCommand[0]);
        }
    }
}
