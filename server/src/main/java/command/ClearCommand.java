package command;

import java.io.Serializable;

/**
 * Class for command clear.
 */
public class ClearCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the ClearCommand object with implementation from FileData class.
     *
     * @param cr FileData object
     */
    public ClearCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().clear();
    }

    @Override
    public String toStringWithArgs() {
        return toString();
    }

    /**
     * Gets string representation of the command.
     *
     * @return string representation of the command.
     */
    @Override
    public String toString() {
        return "clear";
    }
}
