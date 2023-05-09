package command;

import java.io.Serializable;

/**
 * Class for command add.
 */
public class AddCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the AddCommand object with implementation from CollectionCommandReceiver class.
     * @param cr CollectionCommandReceiver object
     */
    public AddCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().add(objectArgs);
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
        return "add";
    }
}
