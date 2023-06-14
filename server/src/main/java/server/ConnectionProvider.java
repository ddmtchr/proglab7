package server;

import processing.CollectionManager;
import stored.LabWork;
import utility.Request;
import utility.Response;
import utility.ResponseType;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConnectionProvider {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final DatagramSocket socket;
    private final int BUFFER_SIZE = 8 * 1024;

    public ConnectionProvider(int port) throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        socket = datagramChannel.socket();
        socket.bind(new InetSocketAddress(port));
        socket.setSoTimeout(100);
        System.out.println("Сервер запущен. Порт: " + port);
    }

    public synchronized void send(Response response) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(response);
            oos.close();
            byte[] byteData = baos.toByteArray();
            logger.log(Level.INFO, String.valueOf(byteData.length));

            byte[][] chunks = new byte[(int)Math.ceil((double)(byteData.length) / (BUFFER_SIZE - 1))][BUFFER_SIZE - 1];
            int startIndex = 0;
            for (int i = 0; i < chunks.length; i++) {
                chunks[i] = Arrays.copyOfRange(byteData, startIndex, startIndex + BUFFER_SIZE - 1);
                startIndex += BUFFER_SIZE - 1;
            }
            logger.log(Level.INFO, "Сообщение разбито на " + chunks.length + " чанков");
            for(int i = 0; i < chunks.length; i++) {
                byte[] chunk = chunks[i];
                if (i == chunks.length - 1) {
                    byte[] lastChunk = Arrays.copyOf(chunk, chunk.length + 1);
                    lastChunk[lastChunk.length - 1] = 1;
                    DatagramPacket packet = new DatagramPacket(lastChunk, BUFFER_SIZE, response.getAddress());
                    socket.send(packet);
                    logger.log(Level.INFO, "Последний чанк размером " + lastChunk.length + " отправлен на клиент.");
                } else {
                    DatagramPacket dp = new DatagramPacket(ByteBuffer.allocate(BUFFER_SIZE).put(chunk).array(), BUFFER_SIZE, response.getAddress());
                    socket.send(dp);
                    logger.log(Level.INFO, "Чанк размером " + chunk.length + " отправлен на клиент.");
                }
            }
            logger.log(Level.INFO, "Ответ отправлен");
        } catch (SocketException e) {
            System.out.println("Сообщение не лезет в пакет!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request receive() {
        Request request;
        try {
            ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
            DatagramPacket datagramPacket = new DatagramPacket(buf.array(), buf.array().length);

            socket.receive(datagramPacket);
            SocketAddress address = datagramPacket.getSocketAddress();

            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buf.array()));

            request = (Request) objectInputStream.readObject();
            request.setHost(address);
            logger.log(Level.INFO, "Запрос принят");
            return request;
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка сериализации");
            e.printStackTrace();
        }
        return null;
    }
}
