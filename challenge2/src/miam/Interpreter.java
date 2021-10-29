package miam;

import java.util.HashMap;

public class Interpreter {
  Block group;

  public Interpreter(Parser parser) {
    group = parser.Group;
  }

  public void start() throws BareBonesException {
    group.run();
    System.out.println("Finished!");
  }

  public void start(HashMap<Integer, Boolean> breakpoints) throws BareBonesException {
    group.run(breakpoints, group);
    System.out.println("Finished!");
  }
}
