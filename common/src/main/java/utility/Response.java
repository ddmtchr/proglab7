package utility;

import stored.LabWork;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Vector;

public class Response implements Serializable {
    private final ResponseBody responseBody;
    private final ResponseType type;
    private final int execCode;
    private final SocketAddress address;
    public Response(ResponseType type, String text, int ec, SocketAddress address) {
        this.type = type;
        responseBody = new ResponseBody(text);
        execCode = ec;
        this.address = address;
    }

    public class ResponseBody implements Serializable {
        private final String responseText;
        private Vector<LabWork> responseCollection;

        ResponseBody(String responseText) {
            this.responseText = responseText;
            this.responseCollection = null;
        }

        public String getText() {
            return responseText;
        }

        public Vector<LabWork> getCollection() {
            return responseCollection;
        }

        public void setCollection(Vector<LabWork> collection) {
            responseCollection = collection;
        }
    }

    public ResponseBody getBody() {
        return responseBody;
    }

    public ResponseType getType() {
        return type;
    }

    public int getExecCode() {
        return execCode;
    }

    public SocketAddress getAddress() {
        return address;
    }
}
