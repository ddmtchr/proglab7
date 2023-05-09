package command;

import java.io.Serializable;

/**
 * Class for command info.
 */
public class InfoCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the InfoCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public InfoCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().info();
    }

    @Override
    public String toStringWithArgs() {
        return toString();
    }

    /**
     * Gets string representation of the command.
     * @return string representation of the command.
     */
    @Override
    public String toString() {
        return "info";
    }
}
