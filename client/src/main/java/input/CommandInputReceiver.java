package input;

import client.*;
import exceptions.ErrorInScriptException;
import exceptions.NoSuchCommandException;
import stored.LabWork;
import utility.LabWorkStatic;
import utility.Request;
import utility.RequestType;

import java.util.Scanner;
import java.util.Vector;

public class CommandInputReceiver extends InputReceiver {
    private FieldInputReceiver fir;
    private String username;

    public CommandInputReceiver(Scanner s, String username) {
        super(s);
        fir = new FieldInputReceiver(s);
        this.username = username;
    }

    public Request createRequest(ConnectionProvider connectionProvider, Session session) {
        String[] command = parseCommand();
        try {
            if (command[0].isEmpty() || command[0].isBlank()) {
                return null;
            } else if (command[0].equals("exit")) {
                if (command[1].equals(" ")) {
                    System.out.println("Выход из программы...");
                    System.exit(0);
                } else {
                    System.out.println("Использование: exit");
                }
            } else if (command[0].equals("execute_script")) {
                String[] argsArray = command[1].split("\\s+");
                if (argsArray.length != 1) {
                    System.out.println("Использование: execute_script {file_name}");
                } else {
                    ScriptExecutor scriptExecutor = new ScriptExecutor();
                    scriptExecutor.executeScript(command[1], connectionProvider, session);
                }
            } else if (command[0].equals("show")) {
                String[] argsArray = command[1].split("\\s+");
                if (argsArray.length != 0) {
                    System.out.println("Использование: show");
                } else {
                    Vector<LabWork> collection = ClientCollectionManager.getClientCollection();
                    if (!collection.isEmpty()) {
                        for (LabWork lw : collection) {
                            System.out.println(lw.toString());
                        }
                    } else {
                        System.out.println("Коллекция пуста");
                    }
                }
            } else {
                Request request = pack(command, session);
                return request;
            }
        } catch (ErrorInScriptException e) {
            System.out.println("Ошибка ввода команд в скрипте");
        } catch (NoSuchCommandException e) {
            System.out.println("Команды " + command[0] + " не существует, введите help для справки");
        }
        return null;
    }

    public String[] parseCommand() {
        System.out.printf("[%s@forty-two ~]$ ", username);
        String input = getScanner().nextLine().trim();
        String cmd, args;
        if (input.contains(" ")) {
            cmd = input.substring(0, input.indexOf(' '));
            args = input.substring(input.indexOf(' ') + 1).trim();
        } else {
            cmd = input;
            args = " ";
        }
        return new String[]{cmd, args};
    }

    public Request pack(String[] command, Session session) throws NoSuchCommandException, ErrorInScriptException {
        Integer objNumber = AvailableCommands.get(command[0]);
        LabWorkStatic lws = null;
        if (objNumber == null) {
            throw new NoSuchCommandException();
        } else if (objNumber == 1) {
            lws = fir.enterLabWork();
        }
        CommandTemplate cmd = new CommandTemplate(command[0], command[1]);
        return new Request(cmd.getCommandName(), cmd.getArgs(), lws,
                session.getLogin(), session.getPassword(), null, RequestType.COMMAND);
    }

    public FieldInputReceiver getFir() {
        return fir;
    }
}
