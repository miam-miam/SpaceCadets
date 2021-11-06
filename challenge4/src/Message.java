public class Message implements java.io.Serializable {
  public final int user;
  public final String message;

  public Message(int user, String message) {
    this.user = user;
    this.message = message;
  }
}
