package server;

import java.util.Scanner;

public class ServerCommandInputReceiver {
    private Scanner scanner;

    public ServerCommandInputReceiver(Scanner s) {
        scanner = s;
    }

    public String[] parseCommand() {
        String input = scanner.nextLine().trim();
        String cmd, args;
        if (input.contains(" ")) {
            cmd = input.substring(0, input.indexOf(' '));
            args = input.substring(input.indexOf(' ') + 1).trim();
        } else {
            cmd = input;
            args = " ";
        }
        return new String[]{cmd, args};
    }
}
