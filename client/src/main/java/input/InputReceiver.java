package input;

import java.util.Scanner;

public class InputReceiver {
    protected Scanner scanner;
    protected boolean fileReadMode;

    public InputReceiver(Scanner s) {
        scanner = s;
        fileReadMode = false;
    }

    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Set the InputReceiver mode.
     * @param fileMode Mode (true - read script from file, false - read strings from console)
     */
    public void setFileReadMode(boolean fileMode) {
        fileReadMode = fileMode;
    }
}
