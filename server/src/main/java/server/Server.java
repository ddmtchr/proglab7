package server;

import database.DBConnector;
import database.DBManager;
import processing.CollectionManager;
import processing.ServerCommandExecutor;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static boolean isRunning;
    private int port;
    private Scanner scanner;
    private ConnectionProvider connectionProvider;
    private final DBConnector dbConnector = new DBConnector();
    private final DBManager dbManager = new DBManager(dbConnector);
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public Server(int port) {
        this.port = port;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        try {
            isRunning = true;
            dbManager.initializeDB();
            logger.log(Level.INFO, "База данных инициализирована");

            initCollection();
            logger.log(Level.INFO, "Коллекция инициализирована");

            connectionProvider = new ConnectionProvider(port);
            ServerCommandInputReceiver scir = new ServerCommandInputReceiver(scanner);
            ServerCommandExecutor serverCommandExecutor = new ServerCommandExecutor();

            Thread requestThread = new Thread(new RequestThread(connectionProvider));
            Thread consoleThread = new Thread(new ConsoleThread(scir, serverCommandExecutor));

            requestThread.start();
            consoleThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCollection() {
        if (dbManager.getCollectionFromDB()) CollectionManager.setLastInitTime();
        else Server.shutdown();
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static void shutdown() {
        isRunning = false;
    }
}
