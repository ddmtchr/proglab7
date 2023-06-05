package server;

import processing.RequestProcessor;
import utility.Request;
import utility.Response;
import utility.ResponseBuilder;

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
                                return new Response(ResponseBuilder.getAndClear(), execCode, receivedRequest.getHost());
                            }, processingThread)
                            .thenAcceptAsync(response -> connectionProvider.send(response), forkJoinPool);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.WARNING, "AAAAAAAAAAAAAAAAAAAAAAAAAAA");
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
}
