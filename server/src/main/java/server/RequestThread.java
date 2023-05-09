package server;

import processing.RequestProcessor;
import utility.Request;
import utility.Response;
import utility.ResponseBuilder;

import java.net.SocketAddress;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestThread implements Runnable {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private ConnectionProvider connectionProvider;
    private RequestProcessor requestProcessor;
    private final ExecutorService requestReceivingExecutor;
    private final ExecutorService requestProcessingExecutor;
    private final ForkJoinPool responseSendingExecutor;
    private final BlockingQueue<Request> requestQueue;
    private final BlockingQueue<Response> responseQueue;

    public RequestThread(ConnectionProvider connectionProvider) {
        requestReceivingExecutor = Executors.newSingleThreadExecutor();
        requestProcessingExecutor = Executors.newSingleThreadExecutor();
        responseSendingExecutor = ForkJoinPool.commonPool();
        requestQueue = new LinkedBlockingQueue<>();
        responseQueue = new LinkedBlockingQueue<>();
        this.connectionProvider = connectionProvider;
    }

    public void run() {
        while (true) {
            try {
                putRequestOnQueue();
                SocketAddress addr = takeRequestAndProcess();
                takeResponseAndSend(addr);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void putRequestOnQueue() throws InterruptedException, ExecutionException {
        Future<Request> waitingFuture = requestReceivingExecutor.submit(connectionProvider::receive);
        Request clientRequest = waitingFuture.get();
        if (clientRequest != null) {
            requestQueue.put(clientRequest);
            logger.log(Level.INFO, "Запрос принят");
        }
    }

    private SocketAddress takeRequestAndProcess() throws InterruptedException, ExecutionException {
        Request clientRequest = requestQueue.take();
        requestProcessor = new RequestProcessor(clientRequest.getUserName(), clientRequest.getUserPassword());
        Future<Response> processingFuture = requestProcessingExecutor.submit(
                () -> {
                    int execCode = requestProcessor.processRequest(clientRequest);
                    if (execCode == 0) logger.log(Level.INFO, "Запрос выполнен");
                    else logger.log(Level.WARNING, "Ошибка выполнения запроса");
                    return new Response(ResponseBuilder.getAndClear(), execCode);
                });
        Response serverResponse = processingFuture.get();
        if (serverResponse != null) {
            responseQueue.put(serverResponse);
            return clientRequest.getHost();
        }
        return null;
    }

    private void takeResponseAndSend(SocketAddress clientHost) throws InterruptedException {
        Response serverResponse = responseQueue.take();
        Future<?> sendingFuture = responseSendingExecutor.submit(() ->
                connectionProvider.send(serverResponse, clientHost));
        logger.log(Level.INFO, "Ответ отправлен");
    }
}
