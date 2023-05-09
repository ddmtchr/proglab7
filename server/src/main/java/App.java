import server.Server;

public class App {
    public static void main(String[] args) {
        try {
            Server server = new Server(Integer.parseInt(args[0]));
            server.run();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Использование: java -jar server.jar {port}");
            System.exit(0);
        }
    }
}
