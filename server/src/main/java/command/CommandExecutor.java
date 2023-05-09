package command;


import database.DBManager;

import java.util.HashMap;

/**
 * Class to check the command exists and execute it.
 */
public class CommandExecutor {
    private HashMap<String, Command> commandMap = new HashMap<>();
    private String username;
    private String password;

    /**
     * Creates the CommandExecutor with given InputReceiver.
     */
    public CommandExecutor(DBManager dbManager, String username, String password) {
        this.username = username;
        this.password = password;
        CollectionCommandReceiver cr = new CollectionCommandReceiver(dbManager, username, password);
        Command[] commandArray = new Command[] {
                new HelpCommand(cr, 0, 0),
                new InfoCommand(cr, 0, 0),
                new ShowCommand(cr, 0, 0),
                new AddCommand(cr, 0, 1),
                new UpdateCommand(cr, 1, 1),
                new RemoveByIdCommand(cr, 1, 0),
                new ClearCommand(cr, 0, 0),
                new InsertAtCommand(cr, 1, 1),
                new AddIfMinCommand(cr, 0, 1),
                new RemoveGreaterCommand(cr, 0, 1),
                new AverageOfMinimalPointCommand(cr, 0, 0),
                new MinByIdCommand(cr, 0, 0),
                new PrintFieldAscendingMinimalPointCommand(cr, 0, 0)
        };
        for (Command command : commandArray) {
            register(command.toString(), command);
        }
    }

    /**
     * Adds command to the hashmap.
     * @param key Name of the command
     * @param command Command object
     */
    private void register(String key, Command command) {
        commandMap.put(key, command);
    }

    /**
     * Checks if the command is entered correctly and executes it
     * @return 0 if command executed correctly; 1 if an error occurred while executing;
     * 2 if script was executed and recursion found
     */
    public int execute(Command command, String stringArgs, Object objectArgs) {
        return command.invoke(stringArgs, objectArgs);
    }

    public HashMap<String, Command> getCommandMap() {
        return commandMap;
    }
}
