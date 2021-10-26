package miam;

import java.util.HashMap;

public class Interpreter {
  Block group;
  Variable[] variables;

  public Interpreter(Parser parser) {
    group = parser.Group;
    variables = parser.Variables.values().toArray(new Variable[0]);
  }

  public void start() throws BareBonesException {
    group.run();
    System.out.println("Finished!");
  }

  public void start(HashMap<Integer, Boolean> breakpoints) throws BareBonesException {
    group.run(breakpoints, variables);
    System.out.println("Finished!");
  }
}
