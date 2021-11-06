import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
  public static void main(String[] args) {
    try (ServerSocket server = new ServerSocket(3001)) {
      server.setReuseAddress(true);
      State state = new State();
      int connectionCount = 0;
      while (true) {
        Socket client = server.accept();
        ClientHandler handler = new ClientHandler(client, connectionCount, state);
        connectionCount += 1;
        new Thread(handler).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
