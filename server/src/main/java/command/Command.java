package command;

/**
 * Interface to be implemented by command classes.
 */
public interface Command {
    int invoke(String stringArgs, Object objectArgs);
    int execute(String stringArgs, Object objectArgs);
    boolean checkArgsNumber(String stringArgs);

    String toStringWithArgs();
    int getObjectsNumber();
}
