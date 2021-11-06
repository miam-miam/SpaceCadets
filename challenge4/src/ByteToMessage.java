import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;

public class ByteToMessage {
  public static String string(byte[] data) {
    return new String(data, StandardCharsets.UTF_8);
  }

  public static Message receivedMessage(byte[] data) throws IOException, ClassNotFoundException {
    InputStream is = new ByteArrayInputStream(data);
    ObjectInputStream os = new ObjectInputStream(is);
    return (Message) os.readObject();
  }

  public static int length(byte[] input) {
    return (input[0] << 24) & 0xff000000
        | (input[1] << 16) & 0x00ff0000
        | (input[2] << 8) & 0x0000ff00
        | (input[3]) & 0x000000ff;
  }
}
