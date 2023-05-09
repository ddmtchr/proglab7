import client.Client;


// TODO Разбивка на пакеты, если слишком большое сообщение
// TODO Возможно триггер на дисциплины в БД
public class App {
    public static void main(String[] args) {
        try {
            Client client = new Client(args[0], Integer.parseInt(args[1]));
            client.run();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Использование: java -jar client.jar {host} {port}");
            System.exit(0);
        }
    }
}
