import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class State {
  private final List<Message> messages = new ArrayList<>();
  private List<Integer> currentConsumption = new ArrayList<>();
  private Set<Integer> lowestConsumerId = new HashSet<>();

  public synchronized void addMessage(Message message) {
    messages.add(message);
  }

  public synchronized void registerNewId(int id) {
    currentConsumption.add(id, 0);
    lowestConsumerId.add(id);
  }

  public synchronized Optional<Message> getMessage(int id) {
    if (messages.size() <= currentConsumption.get(id)) {
      return Optional.empty();
    }
    Message message = messages.get(currentConsumption.get(id));
    currentConsumption.set(id, currentConsumption.get(id) + 1);

    if (lowestConsumerId.contains(id)) {
      lowestConsumerId.remove(id);
      if (lowestConsumerId.isEmpty()) {
        int lowestConsumptionLevel = currentConsumption.get(0);
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
        currentConsumption = currentConsumption.stream().map(f -> f - finalLowestConsumptionLevel).collect(
            Collectors.toList());
      }
    }

    if (message.user == id) {
      return Optional.empty();
    } else {
      return Optional.of(message);
    }
  }
}
