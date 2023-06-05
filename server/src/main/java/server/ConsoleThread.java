package server;

import processing.ServerCommandExecutor;

import java.util.NoSuchElementException;

public class ConsoleThread implements Runnable {
    private ServerCommandInputReceiver scir;
    private ServerCommandExecutor executor;

    public ConsoleThread(ServerCommandInputReceiver scir, ServerCommandExecutor executor) {
        this.scir = scir;
        this.executor = executor;
    }

    public void run() {
        try {
            while (Server.isRunning()) {
                String[] serverCommand = scir.parseCommand();
                executor.execute(serverCommand);
            }
        } catch (NoSuchElementException e) {
            Server.shutdown();
        }
    }
}
