import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class MessageToByte {
  private static byte[] toByte(MessageId id, byte[] data) {
    ByteBuffer out = ByteBuffer.allocate(data.length + 5).putInt(data.length);
    out.put((((Integer) id.ordinal()).byteValue()));
    out.put(data);
    return out.array();
  }

  public static byte[] init(String username) {
    return toByte(MessageId.INIT, username.getBytes(StandardCharsets.UTF_8));
  }

  public static byte[] message(String message) {
    return toByte(MessageId.MESSAGE, message.getBytes(StandardCharsets.UTF_8));
  }

  public static byte[] salt(String salt) {
    return toByte(MessageId.SALT, salt.getBytes(StandardCharsets.UTF_8));
  }

  public static byte[] encrypted(Message message) throws IOException {
    ByteArrayOutputStream bObj = new ByteArrayOutputStream();
    ObjectOutputStream out;
    out = new ObjectOutputStream(bObj);
    out.writeObject(message);
    out.close();
    return toByte(MessageId.MESSAGE_RECEIVED, bObj.toByteArray());
  }

  public static byte[] password(String pass) {
    return toByte(MessageId.ADD_PASSWORD, pass.getBytes(StandardCharsets.UTF_8));
  }

  public static byte[] removeUser(int userId) {
    ByteBuffer data = ByteBuffer.allocate(4).putInt(userId);
    return toByte(MessageId.REMOVE_USER, data.array());
  }

  public static byte[] addUser(int userId, String username) {
    byte[] name = username.getBytes(StandardCharsets.UTF_8);
    ByteBuffer data = ByteBuffer.allocate(4 + name.length).putInt(userId).put(name);
    return toByte(MessageId.ADD_USER, data.array());
  }

  public static byte[] receivedMessage(Message message) throws IOException {
    ByteArrayOutputStream bObj = new ByteArrayOutputStream();
    ObjectOutputStream out;
    out = new ObjectOutputStream(bObj);
    out.writeObject(message);
    out.close();
    return toByte(MessageId.MESSAGE_RECEIVED, bObj.toByteArray());
  }

  public static byte[] listUser(HashMap<Integer, String> userList) throws IOException {
    ByteArrayOutputStream bObj = new ByteArrayOutputStream();
    ObjectOutputStream out;
    out = new ObjectOutputStream(bObj);
    out.writeObject(userList);
    out.close();
    return toByte(MessageId.LIST_USER, bObj.toByteArray());
  }
}
