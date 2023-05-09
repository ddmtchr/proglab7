package command;

import java.io.Serializable;

/**
 * Class for command add_if_min.
 */
public class AddIfMinCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the AddIfMinCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public AddIfMinCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().addIfMin(objectArgs);
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
        return "add_if_min";
    }
}
