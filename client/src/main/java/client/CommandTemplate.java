package client;

public class CommandTemplate {
    private String commandName;
    private String args;

    public CommandTemplate(String name, String args) {
        commandName = name;
        this.args = args;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArgs() {
        return args;
    }
}
