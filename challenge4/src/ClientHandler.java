import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientHandler implements Runnable, UserListener {

  final Socket socket;
  final int connectionId;
  final State state;
  String username;
  DataInputStream is;
  OutputStream os;
  List<String> hashedPasswords = new ArrayList<>();

  public ClientHandler(Socket socket, int connectionId, State state) {
    this.socket = socket;
    this.connectionId = connectionId;
    this.state = state;
  }

  @Override
  public void run() {
    try {
      is = new DataInputStream(socket.getInputStream());
      os = socket.getOutputStream();
      byte[] input = new byte[5];
      is.readFully(input);
      byte[] data = new byte[ByteToMessage.integer(input)];
      is.readFully(data);
      MessageId id = MessageId.values[input[4]];
      if (id != MessageId.INIT) {
        System.err.println("Received incorrect initial packet.");
      }
      username = ByteToMessage.string(data);
      state.registerNewUser(connectionId, username, this);
      System.out.println(username + " has connected!");
      Optional<Message> stateMessage;
      os.write(MessageToByte.listUser(state.getUserList()));
      while (!socket.isClosed()) {
        while (is.available() > 1) {
          is.readFully(input);
          data = new byte[ByteToMessage.integer(input)];
          is.readFully(data);
          switch (MessageId.values[input[4]]) {
            case MESSAGE:
              Message message = new Message(connectionId, ByteToMessage.string(data));
              System.out.println("[" + username + "]: " + message.message);
              state.addMessage(message);
              break;
            case MESSAGE_RECEIVED:
              Message received = ByteToMessage.receivedMessage(data);
              received.user = connectionId;
              System.out.println("[" + username + "] sent encrypted message: " + received.message);
              state.addMessage(received);
            case ADD_PASSWORD:
              hashedPasswords.add(ByteToMessage.string(data));
              break;
          }
        }
        while ((stateMessage = state.getMessage(connectionId)).isPresent()) {
          // Only send the message if the client has the potential of sending being able to decrypt
          // it.
          if (stateMessage.get().user != connectionId
              && (stateMessage.get().passwordHash == null
                  || hashedPasswords.contains(stateMessage.get().passwordHash))) {
            os.write(MessageToByte.receivedMessage(stateMessage.get()));
          }
        }
        Thread.sleep(50);
      }
      System.out.println(username + " has disconnected!");
      state.deRegister(connectionId, this);
    } catch (IOException | InterruptedException | ClassNotFoundException e) {
      System.err.println("Got error: " + e.getMessage());
      try {
        socket.close();
        System.out.println(username + " has disconnected!");
        state.deRegister(connectionId, this);
      } catch (IOException ex) {
        System.err.println(ex.getMessage());
      }
    }
  }

  @Override
  public void onNewUser(int id, String username) {
    try {
      os.write(MessageToByte.addUser(id, username));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onLostUser(int id) {
    try {
      os.write(MessageToByte.removeUser(id));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
