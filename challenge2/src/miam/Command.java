package miam;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

abstract class Command {
  public int lineNumber;

  abstract void run() throws BareBonesException;

  abstract void format(FileWriter fileWriter) throws IOException;

  abstract void py(FileWriter fileWriter) throws IOException;

  abstract void java(FileWriter fileWriter) throws IOException;

  abstract void rust(FileWriter fileWriter) throws IOException;

  abstract void cpp(FileWriter fileWriter) throws IOException;

  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    format(fileWriter);
    if (comments.get(lineNumber) != null) {
      fileWriter.write(" //" + comments.get(lineNumber));
    }
  }

  void run(HashMap<Integer, Boolean> breakpoints, Variable[] Variables) throws BareBonesException {
    if (breakpoints.getOrDefault(lineNumber, false)) {
      System.out.println(
          "Broke at line "
              + lineNumber
              + ". Would you like to set a new breakpoint (b Number) or remove a breakpoint (r Number) or see a named variable (p Name) or even see all variables (p) or continue (c) or skip (s)?");
      Scanner sc = new Scanner(System.in);
      while (true) {
        String choice = sc.nextLine();
        if (choice.equals("p")) {
          for (Variable variable : Variables) {
            if (variable.data != null) {
              System.out.println(variable.name + " is equal to: " + variable.data);
            }
          }
        } else if (choice.startsWith("p") && choice.split("\\s")[1] != null) {
          Optional<Variable> variable =
              Arrays.stream(Variables)
                  .filter(var -> var.name.equals(choice.split("\\s")[1]))
                  .findFirst();
          if (variable.isPresent()) {
            if (variable.get().data == null) {
              System.out.println(variable.get().name + " is currently uninitialised.");
            } else {
              System.out.println(variable.get().name + " is equal to: " + variable.get().data);
            }
          }
        } else if (choice.startsWith("b") && choice.split("\\s")[1] != null) {
          try {
            int breakpoint = Integer.parseInt(choice.split("\\s")[1]);
            breakpoints.put(breakpoint, true);
            System.out.println("Set breakpoint!");
          } catch (NumberFormatException ignored) {
          }
        } else if (choice.startsWith("r") && choice.split("\\s")[1] != null) {
          try {
            int breakpoint = Integer.parseInt(choice.split("\\s")[1]);
            breakpoints.put(breakpoint, false);
            System.out.println("Unset breakpoint!");
          } catch (NumberFormatException ignored) {
          }
        } else if (choice.equals("c")) {
          break;
        } else if (choice.equals("s")) {
          return;
        }
      }
    }
    run();
  }

  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    py(fileWriter);
    if (comments.get(lineNumber) != null) {
      fileWriter.write("  #" + comments.get(lineNumber));
    }
  }

  void java(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    java(fileWriter);
    if (comments.get(lineNumber) != null) {
      fileWriter.write(" //" + comments.get(lineNumber));
    }
  }

  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    rust(fileWriter);
    if (comments.get(lineNumber) != null) {
      fileWriter.write(" //" + comments.get(lineNumber));
    }
  }

  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    cpp(fileWriter);
    if (comments.get(lineNumber) != null) {
      fileWriter.write(" //" + comments.get(lineNumber));
    }
  }
}

class Incr extends Command {
  Variable variable;

  public Incr(Variable Variable, int LineNumber) {
    variable = Variable;
    lineNumber = LineNumber;
  }

  @Override
  void run() throws BareBonesException {
    variable.checkInitialise();
    if (variable.data != Integer.MAX_VALUE) {
      variable.data = variable.data + 1;
    } else {
      throw new BareBonesException("Variable " + variable.name + " has overflowed!");
    }
  }

  @Override
  void format(FileWriter fileWriter) throws IOException {
    fileWriter.write("incr " + variable.name + ";");
  }

  @Override
  void py(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " += 1");
  }

  @Override
  void java(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " += 1;");
  }

  @Override
  void rust(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " += 1;");
  }

  @Override
  void cpp(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " += 1;");
  }
}

class Decr extends Command {
  Variable variable;

  public Decr(Variable Variable, int LineNumber) {
    variable = Variable;
    lineNumber = LineNumber;
  }

  @Override
  void run() throws BareBonesException {
    variable.checkInitialise();
    if (variable.data != 0) {
      variable.data = variable.data - 1;
    } else {
      throw new BareBonesException("Variable " + variable.name + " cannot be negative.");
    }
  }

  @Override
  void format(FileWriter fileWriter) throws IOException {
    fileWriter.write("decr " + variable.name + ";");
  }

  @Override
  void py(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " -= 1");
  }

  @Override
  void java(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " -= 1;");
  }

  @Override
  void rust(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " -= 1;");
  }

  @Override
  void cpp(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " -= 1;");
  }
}

class Clear extends Command {
  Variable variable;

  public Clear(Variable Variable, int LineNumber) {
    variable = Variable;
    lineNumber = LineNumber;
  }

  @Override
  void run() {
    variable.data = 0;
  }

  @Override
  void format(FileWriter fileWriter) throws IOException {
    fileWriter.write("clear " + variable.name + ";");
  }

  @Override
  void py(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " = 0");
  }

  @Override
  void java(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " = 0;");
  }

  @Override
  void rust(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " = 0;");
  }

  @Override
  void cpp(FileWriter fileWriter) throws IOException {
    fileWriter.write(variable.name + " = 0;");
  }
}

class Block extends Command {
  public final List<Command> commands = new LinkedList<>();
  public int depth;

  public Block(int LineNumber, int Depth) {
    lineNumber = LineNumber;
    depth = Depth;
  }

  @Override
  void run() throws BareBonesException {
    for (Command command : commands) {
      command.run();
    }
  }

  @Override
  void run(HashMap<Integer, Boolean> breakpoints, Variable[] Variables) throws BareBonesException {
    for (Command command : commands) {
      command.run(breakpoints, Variables);
    }
  }

  @Override
  void format(FileWriter fileWriter) throws IOException {
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.format(fileWriter);
      fileWriter.write("\n");
    }
  }

  @Override
  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.format(fileWriter, comments);
      fileWriter.write("\n");
    }
  }

  @Override
  void py(FileWriter fileWriter) throws IOException {
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.py(fileWriter);
      fileWriter.write("\n");
    }
  }

  @Override
  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.py(fileWriter, comments);
      fileWriter.write("\n");
    }
  }

  @Override
  void java(FileWriter fileWriter) throws IOException {
    for (Command command : commands) {
      fileWriter.write("  ".repeat(depth + 2));
      command.java(fileWriter);
      fileWriter.write("\n");
    }
  }

  @Override
  void java(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    for (Command command : commands) {
      fileWriter.write("  ".repeat(depth + 2));
      command.java(fileWriter, comments);
      fileWriter.write("\n");
    }
  }

  @Override
  void rust(FileWriter fileWriter) throws IOException {
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth + 1));
      command.rust(fileWriter);
      fileWriter.write("\n");
    }
  }

  @Override
  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth + 1));
      command.rust(fileWriter, comments);
      fileWriter.write("\n");
    }
  }

  @Override
  void cpp(FileWriter fileWriter) throws IOException {
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth + 1));
      command.cpp(fileWriter);
      fileWriter.write("\n");
    }
  }

  @Override
  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth + 1));
      command.cpp(fileWriter, comments);
      fileWriter.write("\n");
    }
  }

  void add(Command command) {
    commands.add(command);
  }
}

class WhileBlock extends Block {
  public Variable variable;

  public WhileBlock(Variable Variable, int LineNumber, int Depth) {
    super(LineNumber, Depth);
    variable = Variable;
  }

  @Override
  void run() throws BareBonesException {
    while (variable.data != 0) {
      for (Command command : commands) {
        command.run();
      }
    }
  }

  @Override
  void run(HashMap<Integer, Boolean> breakpoints, Variable[] Variables) throws BareBonesException {
    while (variable.data != 0) {
      for (Command command : commands) {
        command.run(breakpoints, Variables);
      }
    }
  }

  @Override
  void format(FileWriter fileWriter) throws IOException {
    fileWriter.write("while " + variable.name + " not 0 do;\n");
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.format(fileWriter);
      fileWriter.write("\n");
    }
    fileWriter.write("    ".repeat(depth - 1));
    fileWriter.write("end;");
  }

  @Override
  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("while " + variable.name + " not 0 do;");
    if (comments.get(lineNumber) != null) {
      fileWriter.write(" //" + comments.get(lineNumber) + "\n");
    } else {
      fileWriter.write("\n");
    }
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.format(fileWriter, comments);
      fileWriter.write("\n");
    }
    fileWriter.write("    ".repeat(depth - 1));
    fileWriter.write("end;");
    int endLineNum = commands.get(commands.size() - 1).lineNumber + 1;
    if (comments.get(endLineNum) != null) {
      fileWriter.write(" //" + comments.get(endLineNum));
    }
  }

  @Override
  void py(FileWriter fileWriter) throws IOException {
    fileWriter.write("while " + variable.name + " != 0:\n");
    int i = 0;
    for (Command command : commands) {
      i += 1;
      fileWriter.write("    ".repeat(depth));
      command.py(fileWriter);
      if (i != commands.size()) {
        fileWriter.write("\n");
      }
    }
  }

  @Override
  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("while " + variable.name + " != 0:");
    if (comments.get(lineNumber) != null) {
      fileWriter.write("  #" + comments.get(lineNumber) + "\n");
    } else {
      fileWriter.write("\n");
    }
    int i = 0;
    for (Command command : commands) {
      i += 1;
      fileWriter.write("    ".repeat(depth));
      command.py(fileWriter, comments);
      if (i != commands.size()) {
        fileWriter.write("\n");
      }
    }
    int endLineNum = commands.get(commands.size() - 1).lineNumber + 1;
    if (comments.get(endLineNum) != null) {
      fileWriter.write("\n" + "    ".repeat(depth) + "#" + comments.get(endLineNum));
    }
  }

  @Override
  void java(FileWriter fileWriter) throws IOException {
    fileWriter.write("while (" + variable.name + " != 0) {\n");
    for (Command command : commands) {
      fileWriter.write("  ".repeat(depth + 2));
      command.java(fileWriter);
      fileWriter.write("\n");
    }
    fileWriter.write("  ".repeat(depth + 1) + "}");
  }

  @Override
  void java(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("while (" + variable.name + " != 0) {");
    if (comments.get(lineNumber) != null) {
      fileWriter.write(" //" + comments.get(lineNumber) + "\n");
    } else {
      fileWriter.write("\n");
    }
    for (Command command : commands) {
      fileWriter.write("  ".repeat(depth + 2));
      command.java(fileWriter, comments);
      fileWriter.write("\n");
    }
    int endLineNum = commands.get(commands.size() - 1).lineNumber + 1;
    fileWriter.write("  ".repeat(depth + 1) + "}");
    if (comments.get(endLineNum) != null) {
      fileWriter.write(" //" + comments.get(endLineNum));
    }
  }

  @Override
  void rust(FileWriter fileWriter) throws IOException {
    fileWriter.write("while " + variable.name + " != 0 {\n");
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth + 1));
      command.rust(fileWriter);
      fileWriter.write("\n");
    }
    fileWriter.write("    ".repeat(depth) + "}");
  }

  @Override
  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("while " + variable.name + " != 0 {");
    if (comments.get(lineNumber) != null) {
      fileWriter.write(" //" + comments.get(lineNumber) + "\n");
    } else {
      fileWriter.write("\n");
    }
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth + 1));
      command.rust(fileWriter, comments);
      fileWriter.write("\n");
    }
    int endLineNum = commands.get(commands.size() - 1).lineNumber + 1;
    fileWriter.write("    ".repeat(depth) + "}");
    if (comments.get(endLineNum) != null) {
      fileWriter.write(" //" + comments.get(endLineNum));
    }
  }

  @Override
  void cpp(FileWriter fileWriter) throws IOException {
    fileWriter.write("while (" + variable.name + " != 0) {\n");
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth + 1));
      command.cpp(fileWriter);
      fileWriter.write("\n");
    }
    fileWriter.write("    ".repeat(depth) + "}");
  }

  @Override
  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("while (" + variable.name + " != 0) {");
    if (comments.get(lineNumber) != null) {
      fileWriter.write(" //" + comments.get(lineNumber) + "\n");
    } else {
      fileWriter.write("\n");
    }
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth + 1));
      command.cpp(fileWriter, comments);
      fileWriter.write("\n");
    }
    int endLineNum = commands.get(commands.size() - 1).lineNumber + 1;
    fileWriter.write("    ".repeat(depth) + "}");
    if (comments.get(endLineNum) != null) {
      fileWriter.write(" //" + comments.get(endLineNum));
    }
  }
}
