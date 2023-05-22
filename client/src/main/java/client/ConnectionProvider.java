package client;

import utility.Request;
import utility.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class ConnectionProvider {

    private final DatagramSocket datagramSocket;
    private final int port;

    public ConnectionProvider(String hostName, int port) throws IOException {
        this.port = port;
        datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(5000);
        System.out.println("Подключен к серверу " + hostName + ". Порт: " + port);
    }

    public void send(Request request) {
        ObjectOutputStream oos = null;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(out);
            oos.writeObject(request);

            InetAddress host = InetAddress.getLocalHost();
            DatagramPacket datagramPacket = new DatagramPacket(out.toByteArray(), out.size(), host, port);

            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert oos != null;
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Response receive() throws SocketTimeoutException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        Response response;
        DatagramPacket datagramPacket = new DatagramPacket(buffer.array(), buffer.array().length);
        try {
            datagramSocket.receive(datagramPacket);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
            response = (Response) ois.readObject();
            System.out.println(response.getBody().getBytes().length);
            return response;
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
