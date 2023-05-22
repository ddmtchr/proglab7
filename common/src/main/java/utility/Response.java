package utility;

import java.io.Serializable;
import java.net.SocketAddress;

public class Response implements Serializable {
    private final String responseBody;
    private final int execCode;
    private final SocketAddress address;
    public Response(String b, int ec, SocketAddress address) {
        responseBody = b;
        execCode = ec;
        this.address = address;
    }

    public String getBody() {
        return responseBody;
    }

    public int getExecCode() {
        return execCode;
    }

    public SocketAddress getAddress() {
        return address;
    }
}
