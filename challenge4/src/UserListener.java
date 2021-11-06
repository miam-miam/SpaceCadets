import java.util.EventListener;

public interface UserListener extends EventListener {
  void onNewUser(int id, String username);
  void onLostUser(int id);
}