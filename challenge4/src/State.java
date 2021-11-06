import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class State {
  private final List<Message> messages = new ArrayList<>();
  private final List<Integer> currentConsumption = new ArrayList<>();
  private List<Integer> lowestConsumerId = new ArrayList<>();

  public synchronized void addMessage(Message message) {
    messages.add(message);
  }

  public synchronized void registerNewId(int id) {
    currentConsumption.add(id, 0);
    lowestConsumerId.add(id);
  }

  public synchronized Optional<Message> getMessage(int id) {
    if (messages.size() > currentConsumption.get(id)) {
      Message message = messages.get(currentConsumption.get(id));
      currentConsumption.set(id, currentConsumption.get(id) + 1);

      if (message.user == id) {
        return Optional.empty();
      }

      if (lowestConsumerId.contains(id)) {
        lowestConsumerId.remove(Integer.valueOf(id));
        if (lowestConsumerId.isEmpty()) {
          int lowestConsumptionLevel = currentConsumption.get(0);
          lowestConsumerId = new ArrayList<>(List.of(new Integer[] {0}));
          for (int consumeId = 1; consumeId < currentConsumption.size(); consumeId++) {
            if (lowestConsumptionLevel > currentConsumption.get(consumeId)) {
              lowestConsumptionLevel = currentConsumption.get(consumeId);
              lowestConsumerId = new ArrayList<>(List.of(new Integer[] {consumeId}));
            } else if (lowestConsumptionLevel == currentConsumption.get(consumeId)) {
              lowestConsumerId.add(consumeId);
            }
          }
          messages.subList(0, lowestConsumptionLevel).clear();
        }
        return Optional.of(message);
      }
    }
    return Optional.empty();
  }
}
