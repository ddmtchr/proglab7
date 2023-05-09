package server;

import processing.ServerCommandExecutor;

public class ConsoleThread implements Runnable {
    private ServerCommandInputReceiver scir;
    private ServerCommandExecutor executor;

    public ConsoleThread(ServerCommandInputReceiver scir, ServerCommandExecutor executor) {
        this.scir = scir;
        this.executor = executor;
    }

    public void run() {
        while (true) {
            String[] serverCommand = scir.parseCommand();
            executor.execute(serverCommand);
        }
    }
}
