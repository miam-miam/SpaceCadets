import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
  public static void main(String[] args) throws IOException {
    BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
    String username = obj.readLine();
    byte[] input = new byte[5];
    try (Socket socket = new Socket("localhost", 3001)) {
      OutputStream os = socket.getOutputStream();
      DataInputStream is = new DataInputStream(socket.getInputStream());
      os.write(MessageToByte.init(username));
      while (true) {
        if (obj.ready()) {
          os.write(MessageToByte.message(obj.readLine()));
        }
        if (is.available() > 1) {
          is.readFully(input);
          byte[] data = new byte[ByteToMessage.length(input)];
          is.readFully(data);
          if (MessageId.values[input[4]] == MessageId.MESSAGE_RECEIVED) {
            Message message = ByteToMessage.receivedMessage(data);
            System.out.println("[" + message.user + "]" + message.message);
          }
        }
        Thread.sleep(50);
      }
    } catch (IOException | ClassNotFoundException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
