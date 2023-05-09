package command;

import java.io.Serializable;

/**
 * Class for command show.
 */
public class ShowCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the ShowCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public ShowCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().show();
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
        return "show";
    }
}
