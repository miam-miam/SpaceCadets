public class User implements java.io.Serializable {

  public final int userId;
  public final String username;

  public User(int userId, String username) {
    this.userId = userId;
    this.username = username;
  }
}
