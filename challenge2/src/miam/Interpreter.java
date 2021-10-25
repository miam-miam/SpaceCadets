package miam;

public class Interpreter {
  Integer[] Vars;
  Command[] Instructions; // Using array as much faster to access than list
  Loop[] Loops;

  public Interpreter(Parser parser) {
    Vars = new Integer[parser.Vars.size()];
    Instructions = parser.Instructions.toArray(new Command[0]);
    Loops = parser.Loops.toArray(new Loop[0]);
  }

  public void start() {
    for (int i = 0; i < Instructions.length; i++) {
      switch (Instructions[i].Type) {
        case INCR:
          Vars[Instructions[i].Id] += 1;
          break;
        case DECR:
          Vars[Instructions[i].Id] -= 1;
          break;
        case CLEAR:
          Vars[Instructions[i].Id] = 0;
          break;
        case WHILE:
          if (Vars[Loops[Instructions[i].Id].Variable] == 0) {
            i = Loops[Instructions[i].Id].End;
          }
          break;
        case END:
          if (Vars[Loops[Instructions[i].Id].Variable] != 0) {
            i = Loops[Instructions[i].Id].Start;
          }
          break;
      }
    }
    System.out.println("Finished!");
  }
}
