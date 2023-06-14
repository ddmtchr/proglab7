package utility;

public enum RequestType {
    COMMAND,
    REGISTER,
    LOGIN,
    GET_SALT,
    GET_COLLECTION;

    public ResponseType toResponseType() {
        return ResponseType.valueOf(this.toString());
    }
}
