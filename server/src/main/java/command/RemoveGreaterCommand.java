package command;

import java.io.Serializable;

/**
 * Class for command remove_greater.
 */
public class RemoveGreaterCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the RemoveGreaterCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public RemoveGreaterCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().removeGreater(objectArgs);
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
        return "remove_greater";
    }
}
