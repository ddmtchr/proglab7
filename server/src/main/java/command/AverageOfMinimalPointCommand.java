package command;

import java.io.Serializable;

/**
 * Class for command average_of_minimal_point.
 */
public class AverageOfMinimalPointCommand extends AbstractCommand implements Command, Serializable {

    /**
     * Creates the AverageOfMinimalPointCommand object with implementation from FileData class.
     * @param cr FileData object
     */
    public AverageOfMinimalPointCommand(CollectionCommandReceiver cr, int argsNumber, int obNumber) {
        super(cr, argsNumber, obNumber);
    }

    @Override
    public int execute(String stringArgs, Object objectArgs) {
        return getImplementations().averageOfMinimalPoint();
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
        return "average_of_minimal_point";
    }
}
