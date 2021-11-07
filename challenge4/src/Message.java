public class Message implements java.io.Serializable {
  public int user;
  public final String message;
  public String passwordHash;

  public Message(int user, String message) {
    this.user = user;
    this.message = message;
  }

  public Message(String passwordHash, String message) {
    this.message = message;
    this.passwordHash = passwordHash;
  }
}
