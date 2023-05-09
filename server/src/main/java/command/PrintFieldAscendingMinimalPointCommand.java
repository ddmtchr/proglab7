package command;

import java.io.Serializable;

/**
 * Class for command print_field_ascending_minimal_point.
 */
public class PrintFieldAscendingMinimalPointCommand extends AbstractCommand implements Command, Serializable {
    /**
     * Creates the PrintFieldAscendingMinimalPointCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public PrintFieldAscendingMinimalPointCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().printFAMinimalPoint();
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
        return "print_field_ascending_minimal_point";
    }
}
