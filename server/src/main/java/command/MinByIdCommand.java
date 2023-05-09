package command;

import java.io.Serializable;

/**
 * Class for command min_by_id.
 */
public class MinByIdCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the MinByIdCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public MinByIdCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().minById();
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
        return "min_by_id";
    }
}
