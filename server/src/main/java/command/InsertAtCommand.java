package command;

import java.io.Serializable;

/**
 * Class for command insert_at.
 */
public class InsertAtCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the InsertAtCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public InsertAtCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().insertAt(stringArgs, objectArgs);
    }

    @Override
    public String toStringWithArgs() {
        return toString() + " {index}";
    }

    /**
     * Gets string representation of the command.
     * @return string representation of the command.
     */
    @Override
    public String toString() {
        return "insert_at";
    }
}
