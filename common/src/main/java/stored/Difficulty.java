package stored;

/**
 * Enum that contains levels of difficulty of LabWork.
 */
public enum Difficulty {
    VERY_EASY,
    NORMAL,
    IMPOSSIBLE,
    TERRIBLE;

    /**
     * Prints the list of enum constants.
     */
    public static void printNameList() {
        for (Difficulty d: Difficulty.values()) {
            System.out.println(d);
        }
    }
}
