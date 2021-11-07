import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Client {
  // Made using BCrypt.gensalt(12)
  static final String SALT = "$2a$12$gn6Pb952p4ofjcdMHOYOpe";

  public static void main(String[] args) throws IOException {
    BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("Please input a username: ");
    String username = obj.readLine();
    HashMap<Integer, String> users = new HashMap<>();
    List<String> passwords = new ArrayList<>();
    List<String> hashedPasswords = new ArrayList<>();
    byte[] input = new byte[5];
    try (Socket socket = new Socket("localhost", 3001)) {
      OutputStream os = socket.getOutputStream();
      DataInputStream is = new DataInputStream(socket.getInputStream());
      os.write(MessageToByte.init(username));
      while (true) {
        if (obj.ready()) {
          String userInput = obj.readLine();
          if (userInput.startsWith("/password ")) {
            String password = userInput.substring(10);
            String hashedPassword = BCrypt.hashpw(password, SALT);
            System.out.println("Set new password!");
            passwords.add(password);
            hashedPasswords.add(hashedPassword);
            os.write(MessageToByte.password(hashedPassword));
          } else {
            if (passwords.isEmpty()) {
              os.write(MessageToByte.message(userInput));
            } else {
              os.write(
                  MessageToByte.encrypted(
                      new Message(
                          hashedPasswords.get(hashedPasswords.size() - 1),
                          plainToEncrypted(passwords.get(passwords.size() - 1), userInput))));
            }
          }
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
              String text = message.message;
              if (message.passwordHash != null) {
                for (String password : passwords) {
                  if (BCrypt.checkpw(password, message.passwordHash)) {
                    text = encryptedToPlain(password, message.message);
                    break;
                  }
                }
              }
              System.out.println("[" + users.getOrDefault(message.user, "Unknown") + "]: " + text);
              break;
          }
        }
        Thread.sleep(50);
      }
    } catch (IOException | ClassNotFoundException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static String rot(int times, String message) {
    StringBuilder newMessage = new StringBuilder();
    for (char c : message.toCharArray()) {
      newMessage.append((char) ((int) c + times));
    }
    return newMessage.toString();
  }

  public static String encryptedToPlain(String password, String encryptedMessage) {
    return rot(-(password.hashCode() % 13), encryptedMessage);
  }

  public static String plainToEncrypted(String password, String message) {
    return rot(password.hashCode() % 13, message);
  }
}
