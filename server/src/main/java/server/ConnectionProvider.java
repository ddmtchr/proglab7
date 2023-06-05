package server;

import utility.Request;
import utility.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionProvider {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final DatagramSocket socket;

    public ConnectionProvider(int port) throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        socket = datagramChannel.socket();
        socket.bind(new InetSocketAddress(port));
        socket.setSoTimeout(100);
        System.out.println("Сервер запущен. Порт: " + port);
    }

    public void send(Response response) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(response);
            oos.flush();
            DatagramPacket responsePacket = new DatagramPacket(baos.toByteArray(), baos.size());
            responsePacket.setSocketAddress(response.getAddress());

            socket.send(responsePacket);
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
            ByteBuffer buf = ByteBuffer.allocate(8192);
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
