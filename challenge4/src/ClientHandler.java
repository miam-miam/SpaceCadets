import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

public class ClientHandler implements Runnable {

  final Socket socket;
  final int connectionId;
  final State state;
  String username;

  public ClientHandler(Socket socket, int connectionId, State state) {
    System.out.println(connectionId);
    this.socket = socket;
    this.connectionId = connectionId;
    this.state = state;
  }

  @Override
  public void run() {
    try {
      DataInputStream is = new DataInputStream(socket.getInputStream());
      OutputStream os = socket.getOutputStream();
      byte[] input = new byte[5];
      is.readFully(input);
      byte[] data = new byte[ByteToMessage.length(input)];
      is.readFully(data);
      MessageId id = MessageId.values[input[4]];
      if (id != MessageId.INIT) {
        System.err.println("Received incorrect initial packet.");
      }
      username = ByteToMessage.string(data);
      state.registerNewId(connectionId);
      System.out.println(username + " has connected!");
      Optional<Message> stateMessage;
      while (!socket.isClosed()) {
        if (is.available() > 1) {
          is.readFully(input);
          data = new byte[ByteToMessage.length(input)];
          is.readFully(data);
          if (MessageId.values[input[4]] == MessageId.MESSAGE) {
            Message message = new Message(connectionId, ByteToMessage.string(data));
            System.out.println("[" + message.user + "]" + message.message);
            state.addMessage(message);
          }
        }
        while ((stateMessage = state.getMessage(connectionId)).isPresent()) {
          System.out.println("Sending message: " + stateMessage.get().message);
          os.write(MessageToByte.receivedMessage(stateMessage.get()));
        }
        Thread.sleep(50);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Got error: " + e.getMessage());
      try {
        socket.close();
      } catch (IOException ex) {
        System.err.println(ex.getMessage());
      }
    }
  }
}
