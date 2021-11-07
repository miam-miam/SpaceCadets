import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class State {
  private final List<Message> messages = new ArrayList<>();
  private final HashMap<Integer, Integer> currentConsumption = new HashMap<>();
  private Set<Integer> lowestConsumerId = new HashSet<>();
  private final HashMap<Integer, String> userList = new HashMap<>();
  private final HashSet<UserListener> listeners = new HashSet<>();

  public synchronized void addMessage(Message message) {
    messages.add(message);
  }

  public synchronized void registerNewUser(int id, String username, UserListener listener) {
    currentConsumption.put(id, 0);
    lowestConsumerId.add(id);
    userList.put(id, username);
    for (UserListener listen : listeners) {
      listen.onNewUser(id, username);
    }
    listeners.add(listener);
  }

  public synchronized void deRegister(int id, UserListener listener) {
    listeners.remove(listener);
    for (UserListener listen : listeners) {
      listen.onLostUser(id);
    }
    lowestConsumerId.remove(id);
    currentConsumption.remove(id);
  }

  public synchronized HashMap<Integer, String> getUserList() {
    return userList;
  }

  public synchronized Optional<Message> getMessage(int id) {
    if (messages.size() <= currentConsumption.get(id)) {
      return Optional.empty();
    }
    Message message = messages.get(currentConsumption.get(id));
    currentConsumption.put(id, currentConsumption.get(id) + 1);

    if (lowestConsumerId.contains(id)) {
      lowestConsumerId.remove(id);
      if (lowestConsumerId.isEmpty()) {
        int lowestConsumptionLevel = Integer.MAX_VALUE;
        lowestConsumerId = new HashSet<>(List.of(new Integer[] {0}));
        for (int consumeId = 1; consumeId < currentConsumption.size(); consumeId++) {
          if (lowestConsumptionLevel > currentConsumption.get(consumeId)) {
            lowestConsumptionLevel = currentConsumption.get(consumeId);
            lowestConsumerId = new HashSet<>(List.of(new Integer[] {consumeId}));
          } else if (lowestConsumptionLevel == currentConsumption.get(consumeId)) {
            lowestConsumerId.add(consumeId);
          }
        }
        messages.subList(0, lowestConsumptionLevel).clear();

        // Made messages smaller so now need to re-normalise to new message size.
        int finalLowestConsumptionLevel = lowestConsumptionLevel;
        currentConsumption.replaceAll((k, v) -> v - finalLowestConsumptionLevel);
      }
    }
    return Optional.of(message);
  }
}
