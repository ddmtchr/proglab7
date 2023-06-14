package server;

import processing.CollectionManager;
import processing.RequestProcessor;
import stored.Coordinates;
import stored.Difficulty;
import stored.Discipline;
import stored.LabWork;
import utility.Request;
import utility.RequestType;
import utility.Response;
import utility.ResponseBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestThread implements Runnable {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private ConnectionProvider connectionProvider;
    private ExecutorService receivingThread;
    private ExecutorService processingThread;
    private ForkJoinPool forkJoinPool;

    public RequestThread(ConnectionProvider connectionProvider) {
        receivingThread = Executors.newSingleThreadExecutor();
        processingThread = Executors.newSingleThreadExecutor();
        forkJoinPool = ForkJoinPool.commonPool();
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void run() {
        while (Server.isRunning()) {
            try {
                Future<Request> requestFuture = receivingThread.submit(connectionProvider::receive);
                Request request = requestFuture.get();
                if (request != null) {
                    CompletableFuture
                            .supplyAsync(() -> request)
                            .thenApplyAsync(receivedRequest -> {
                                RequestProcessor requestProcessor = new RequestProcessor(
                                        receivedRequest.getUserName(), receivedRequest.getUserPassword());
                                int execCode = requestProcessor.processRequest(receivedRequest);
                                if (execCode == 0) logger.log(Level.INFO, "Запрос выполнен");
                                else logger.log(Level.WARNING, "Ошибка выполнения запроса");
                                requestProcessor.closeDBConnection();
                                Response response = new Response(receivedRequest.getType().toResponseType(),
                                        ResponseBuilder.getAndClear(),
                                        execCode, receivedRequest.getHost());
                                if (request.getType() == RequestType.GET_COLLECTION)
                                    response.getBody().setCollection(CollectionManager.getCollection());
                                return response;
                            }, processingThread)
                            .thenAcceptAsync(response -> connectionProvider.send(response), forkJoinPool);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.WARNING, "AAAAAAAAAAAAAAAAAAAAAAAAAAAUCH");
                e.printStackTrace();
            }
        }
        try {
            receivingThread.shutdown();
            processingThread.shutdown();
            forkJoinPool.shutdown();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Ошибка при выключении сервера");
        }
    }

    private ArrayList<LabWork> getTestCollection() {
        ArrayList<LabWork> coll = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            coll.add(new LabWork(333, "asdas", new Coordinates(34, 53F), ZonedDateTime.now(), 43, 12, Difficulty.IMPOSSIBLE, new Discipline("gigchad", 43, 87, 47), "testuser"));
        }
        return coll;
    }
}
