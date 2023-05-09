package utility;

import java.io.Serializable;
import java.net.SocketAddress;

public class Request implements Serializable {
    private SocketAddress host;
    private final String cmd;
    private final String stringArgs;
    private final Serializable objectArgs;
    private final RequestType type;
    private final String userLogin;
    private final String userPassword;
    private final String userSalt;

    public Request(String cmd, String stringArgs, Serializable objectArgs,
                   String userLogin, String userPassword, String userSalt, RequestType type) {
        this.cmd = cmd;
        this.stringArgs = stringArgs;
        this.objectArgs = objectArgs;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userSalt = userSalt;
        this.type = type;
    }

    public String getCommand() {
        return cmd;
    }

    public String getStringArgs() {
        return stringArgs;
    }

    public Serializable getObjectArgs() {
        return objectArgs;
    }

    public String getUserName() {
        return userLogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public RequestType getType() {
        return type;
    }

    public String getUserSalt() {
        return userSalt;
    }

    public void setHost(SocketAddress host) {
        this.host = host;
    }

    public SocketAddress getHost() {
        return host;
    }

}
