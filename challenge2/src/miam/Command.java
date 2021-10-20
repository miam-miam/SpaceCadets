package miam;

public class Command {
  public enum CommandType {
    INCR,
    DECR,
    CLEAR,
    WHILE,
    END
  }

  public CommandType Type;
  public int Id;

  public Command(CommandType type, int id) {
    Type = type;
    Id = id;
  }
}
