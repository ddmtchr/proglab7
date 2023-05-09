package command;

import java.io.Serializable;

/**
 * Class for command update.
 */
public class UpdateCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the UpdateCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public UpdateCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }


    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().update(stringArgs, objectArgs);
    }

    @Override
    public String toStringWithArgs() {
        return toString() + " {id}";
    }

    /**
     * Gets string representation of the command.
     * @return string representation of the command.
     */
    @Override
    public String toString() {
        return "update";
    }
}
