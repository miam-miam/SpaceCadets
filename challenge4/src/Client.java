import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class Client {
  public static void main(String[] args) throws IOException {
    BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
    String username = obj.readLine();
    System.out.print("Please input a username: ");
    HashMap<Integer, String> users = new HashMap<>();
    byte[] input = new byte[5];
    try (Socket socket = new Socket("localhost", 3001)) {
      OutputStream os = socket.getOutputStream();
      DataInputStream is = new DataInputStream(socket.getInputStream());
      os.write(MessageToByte.init(username));
      while (true) {
        if (obj.ready()) {
          os.write(MessageToByte.message(obj.readLine()));
        }
        while (is.available() > 1) {
          is.readFully(input);
          byte[] data = new byte[ByteToMessage.integer(input)];
          is.readFully(data);
          switch (MessageId.values[input[4]]) {
            case ADD_USER:
              int newId = ByteToMessage.integer(data);
              String newUsername = ByteToMessage.string(Arrays.copyOfRange(data, 4, data.length));
              users.put(newId, newUsername);
              System.out.println(newUsername + " has connected!");
              break;
            case REMOVE_USER:
              newId = ByteToMessage.integer(data);
              System.out.println(users.get(newId) + " has disconnected!");
              users.remove(newId);
              break;
            case LIST_USER:
              users.putAll(ByteToMessage.listUser(data));
              break;
            case MESSAGE_RECEIVED:
              Message message = ByteToMessage.receivedMessage(data);
              System.out.println("[" + users.getOrDefault(message.user, "Unknown") + "]: " + message.message);
              break;
          }
        }
        Thread.sleep(50);
      }
    } catch (IOException | ClassNotFoundException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
