package command;

import java.io.Serializable;

/**
 * Class for command remove_by_id.
 */
public class RemoveByIdCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the RemoveByIdCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public RemoveByIdCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().removeById(stringArgs);
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
        return "remove_by_id";
    }
}
