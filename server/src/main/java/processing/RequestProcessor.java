package processing;

import command.Command;
import command.CommandExecutor;
import database.DBConnector;
import database.DBManager;
import exceptions.WrongCommandFormatException;
import utility.Request;
import utility.RequestType;
import utility.ResponseBuilder;

import java.io.Serializable;

public class RequestProcessor {
    private final DBConnector dbConnector;
    private final DBManager dbManager;
    private final String username;
    private final String password;

    public RequestProcessor(String username, String password) {
        dbConnector = new DBConnector();
        dbManager = new DBManager(dbConnector);
        this.username = username;
        this.password = password;
    }

    public int processRequest(Request request) {
        int execCode;
        if (request.getType() == RequestType.COMMAND) {
            if (dbManager.validateUser(request.getUserName(), request.getUserPassword())) {
                CommandExecutor executor = new CommandExecutor(dbManager, username, password);
                Command cmd = executor.getCommandMap().get(request.getCommand());
                String stringArgs = request.getStringArgs();
                if (validateCommand(cmd, stringArgs)) {
                    Serializable objectArgs = request.getObjectArgs();
                    execCode = executor.execute(cmd, stringArgs, objectArgs);
                } else execCode = 1;
            } else {
                ResponseBuilder.appendln("Вы не авторизованы!");
                execCode = 1;
            }
        } else if (request.getType() == RequestType.REGISTER) {
            execCode = dbManager.registerUser(request.getUserName(), request.getUserPassword(), request.getUserSalt());
        } else if (request.getType() == RequestType.LOGIN) {
            execCode = dbManager.loginUser(request.getUserName(), request.getUserPassword());

        } else if (request.getType() == RequestType.GET_SALT) {
            execCode = dbManager.getSaltByLogin(request.getUserName());
        } else {
            execCode = 1;
            ResponseBuilder.appendln("Неверный тип запроса");
        }
        return execCode;
    }

    public boolean validateCommand(Command cmd, String stringArgs) {
        try {
            if (!cmd.checkArgsNumber(stringArgs)) throw new WrongCommandFormatException();
            return true;
        } catch (WrongCommandFormatException e) {
            ResponseBuilder.appendln("Использование: " + cmd.toStringWithArgs());
            return false;
        }
    }
}
