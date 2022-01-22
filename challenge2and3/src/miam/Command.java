package miam;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * The Command class is used to store all commands that the parser has parsed. It also contains
 * references to variables and as such recursively executes when called by the Interpreter.
 */
abstract class Command {
  public int lineNumber;

  abstract void run() throws BareBonesException;

  void comment(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    if (comments.get(lineNumber) != null) {
      fileWriter.write(" // " + comments.get(lineNumber));
    }
  }

  void pyComment(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    if (comments.get(lineNumber) != null) {
      fileWriter.write("  # " + comments.get(lineNumber));
    }
  }

  void run(HashMap<Integer, Boolean> breakpoints, Block group) throws BareBonesException {
    debug(breakpoints, group);
    run();
  }

  void debug(HashMap<Integer, Boolean> breakpoints, Block group) {
    if (breakpoints.getOrDefault(lineNumber, false)) {
      System.out.println(
          "Broke at line "
              + lineNumber
              + ". Would you like to set a new breakpoint (b Number) or remove a breakpoint (r Number) or see a named variable (p Name) or even see all variables (p) or continue (c) or skip (s)?");
      Scanner sc = new Scanner(System.in);
      while (true) {
        String choice = sc.nextLine();
        if (choice.equals("p")) {
          for (Variable variable : group.GetAllVariables()) {
            if (variable.data != null) {
              System.out.println(variable.name + " is equal to: " + variable.data);
            }
          }
        } else if (choice.startsWith("p") && choice.split("\\s")[1] != null) {
          Optional<Variable> variable =
              group.GetAllVariables().stream()
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
  }

  abstract void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException;

  abstract void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException;

  abstract void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException;

  abstract void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException;
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
  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("incr " + variable.name + ";");
    comment(fileWriter, comments);
  }

  @Override
  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(variable.name + " += 1");
    pyComment(fileWriter, comments);
  }

  @Override
  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(variable.name + " += 1;");
    comment(fileWriter, comments);
  }

  @Override
  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(variable.name + " += 1;");
    comment(fileWriter, comments);
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
  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("decr " + variable.name + ";");
    comment(fileWriter, comments);
  }

  @Override
  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(variable.name + " -= 1");
    pyComment(fileWriter, comments);
  }

  @Override
  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(variable.name + " -= 1;");
    comment(fileWriter, comments);
  }

  @Override
  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(variable.name + " -= 1;");
    comment(fileWriter, comments);
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
  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("clear " + variable.name + ";");
    comment(fileWriter, comments);
  }

  @Override
  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(variable.name + " = 0");
    pyComment(fileWriter, comments);
  }

  @Override
  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(variable.name + " = 0;");
    comment(fileWriter, comments);
  }

  @Override
  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(variable.name + " = 0;");
    comment(fileWriter, comments);
  }
}

class Func extends Command {
  Variable[] args;
  FuncBlock funcBlock;
  boolean[] references;

  public Func(Variable[] Args, FuncBlock FuncBlock, boolean[] References, int LineNumber) {
    funcBlock = FuncBlock;
    args = Args;
    lineNumber = LineNumber;
    references = References;
  }

  @Override
  void run() throws BareBonesException {
    int i = 0;
    for (String arg : funcBlock.args) {
      Variable arg_func = funcBlock.variables.get(arg);
      arg_func.data = args[i].data;
      i += 1;
    }

    funcBlock.run();

    for (int j = 0; j < args.length; j++) {
      if (references[j]) {
        Variable arg_func = funcBlock.variables.get(funcBlock.args[j]);
        args[j].data = arg_func.data;
      }
    }
  }

  @Override
  void run(HashMap<Integer, Boolean> breakpoints, Block group) throws BareBonesException {
    int i = 0;
    for (String arg : funcBlock.args) {
      Variable arg_func = funcBlock.variables.get(arg.replace("&", ""));
      arg_func.data = args[i].data;
      i += 1;
    }

    debug(breakpoints, group);
    funcBlock.debug(breakpoints, funcBlock);
    funcBlock.run(breakpoints, funcBlock);

    for (int j = 0; j < args.length; j++) {
      if (references[j]) {
        Variable arg_func = funcBlock.variables.get(funcBlock.args[j]);
        args[j].data = arg_func.data;
      }
    }
  }

  @Override
  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write(funcBlock.name + "(");
    for (int i = 0; i < args.length; i++) {
      if (i != 0) {
        fileWriter.write(", ");
      }
      if (references[i]) {
        fileWriter.write("&");
      }
      fileWriter.write(args[i].name);
    }
    fileWriter.write(");");
    comment(fileWriter, comments);
  }

  @Override
  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("(");
    boolean firstInstance = true;
    for (int i = 0; i < references.length; i++) {
      if (references[i]) {
        if (firstInstance) {
          firstInstance = false;
          fileWriter.write(args[i].name);
        } else {
          fileWriter.write(", " + args[i].name);
        }
      } else {
        if (firstInstance) {
          firstInstance = false;
          fileWriter.write("_");
        } else {
          fileWriter.write(", _");
        }
      }
    }
    fileWriter.write(") = ");
    fileWriter.write(funcBlock.name + "(" + args[0].name);
    for (int j = 1; j < args.length; j++) {
      fileWriter.write(", " + args[j].name);
    }
    fileWriter.write(")");
    pyComment(fileWriter, comments);
  }

  @Override
  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    for (int i = 0; i < references.length; i++) {
      if (!references[i]) {
        StringBuilder prefix = new StringBuilder("$");
        for (int j = 0; j < i; j++) {
          if (args[j].name.equals(args[i].name)) {
            prefix.append("$");
          }
        }
        fileWriter.write("int " + prefix + args[i].name + " = " + args[i].name + ";");
      }
    }
    fileWriter.write(funcBlock.name + "(");
    boolean firstInstance = true;
    for (int i = 0; i < references.length; i++) {
      if (references[i]) {
        if (firstInstance) {
          firstInstance = false;
          fileWriter.write(args[i].name);
        } else {
          fileWriter.write(", " + args[i].name);
        }
      } else {
        StringBuilder prefix = new StringBuilder("$");
        for (int j = 0; j < i; j++) {
          if (args[j].name.equals(args[i].name)) {
            prefix.append("$");
          }
        }
        if (firstInstance) {
          firstInstance = false;
          fileWriter.write(prefix + args[i].name);
        } else {
          fileWriter.write(", " + prefix + args[i].name);
        }
      }
    }
    fileWriter.write(");");
    comment(fileWriter, comments);
  }

  @Override
  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("let (");
    boolean firstInstance = true;
    for (int i = 0; i < references.length; i++) {
      if (references[i]) {
        if (firstInstance) {
          firstInstance = false;
          fileWriter.write("mut " + args[i].name);
        } else {
          fileWriter.write(", mut " + args[i].name);
        }
      } else {
        if (firstInstance) {
          firstInstance = false;
          fileWriter.write("_");
        } else {
          fileWriter.write(", _");
        }
      }
    }
    fileWriter.write(") = ");
    fileWriter.write(funcBlock.name + "(" + args[0].name);
    for (int j = 1; j < args.length; j++) {
      fileWriter.write(", " + args[j].name);
    }
    fileWriter.write(");");
    comment(fileWriter, comments);
  }
}

class Block extends Command {
  public final List<Command> commands = new LinkedList<>();
  public final HashMap<String, Variable> variables = new HashMap<>();
  public int depth;

  public Block(int LineNumber, int Depth) {
    lineNumber = LineNumber;
    depth = Depth;
  }

  public List<Variable> GetAllVariables() {
    return new ArrayList<>(variables.values());
  }

  @Override
  void run() throws BareBonesException {
    for (Command command : commands) {
      command.run();
    }
  }

  @Override
  void run(HashMap<Integer, Boolean> breakpoints, Block _group) throws BareBonesException {
    for (Command command : commands) {
      command.run(breakpoints, this);
    }
  }

  @Override
  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    depth -= 1;
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.format(fileWriter, comments);
      fileWriter.write("\n");
    }
    depth += 1;
  }

  @Override
  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    depth -= 1;
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.py(fileWriter, comments);
      fileWriter.write("\n");
    }
    depth += 1;
  }

  @Override
  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    for (String var : variables.keySet()) {
      fileWriter.write("    ".repeat(depth) + "let mut " + var + ": i32;\n");
    }
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.rust(fileWriter, comments);
      fileWriter.write("\n");
    }
  }

  @Override
  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    for (String var : variables.keySet()) {
      fileWriter.write("    ".repeat(depth) + "int " + var + ";\n");
    }
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.cpp(fileWriter, comments);
      fileWriter.write("\n");
    }
  }

  void add(Command command) {
    commands.add(command);
  }

  int getDepth() {
    return depth;
  }
}

class FuncBlock extends Block {
  // Remember to reset vars
  public String[] args;
  public String name;

  public FuncBlock(String[] Args, int lineNumber, String Name) {
    super(lineNumber, 1);
    args = Args;
    name = Name;
    for (String arg : args) {
      variables.put(arg, new Variable(arg));
    }
  }

  @Override
  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("func " + name + "(" + String.join(", ", args) + ");");
    comment(fileWriter, comments);
    fileWriter.write("\n");
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.format(fileWriter, comments);
      fileWriter.write("\n");
    }
    fileWriter.write("end;\n");
  }

  @Override
  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("def " + name + "(" + String.join(", ", args) + "):");
    pyComment(fileWriter, comments);
    fileWriter.write("\n");
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.py(fileWriter, comments);
      fileWriter.write("\n");
    }

    boolean firstInstance = true;
    for (String arg : args) {
      if (firstInstance) {
        firstInstance = false;
        fileWriter.write("    return " + arg);
      } else {
        fileWriter.write(", " + arg);
      }
    }
    fileWriter.write("\n\n\n");
  }

  @Override
  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    StringBuilder returnTypes = new StringBuilder("(i32");
    fileWriter.write("fn " + name + "(mut " + args[0] + ": i32");
    for (int j = 1; j < args.length; j++) {
      fileWriter.write(", mut " + args[j] + ": i32");
      returnTypes.append(", i32");
    }
    returnTypes.append(") {");
    fileWriter.write(") -> " + returnTypes);
    comment(fileWriter, comments);
    fileWriter.write("\n");
    for (String var : variables.keySet()) {
      if (!Arrays.asList(args).contains(var)) {
        fileWriter.write("    let mut " + var + ": i32;\n");
      }
    }
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.rust(fileWriter, comments);
      fileWriter.write("\n");
    }

    boolean firstInstance = true;
    for (String arg : args) {
      if (firstInstance) {
        firstInstance = false;
        fileWriter.write("    return (" + arg);
      } else {
        fileWriter.write(", " + arg);
      }
    }
    fileWriter.write(");\n}\n\n");
  }

  @Override
  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("void " + name + "( int& " + args[0]);
    for (int j = 1; j < args.length; j++) {
      fileWriter.write(", int& " + args[j]);
    }
    fileWriter.write(") {");
    comment(fileWriter, comments);
    fileWriter.write("\n");
    for (String var : variables.keySet()) {
      if (!Arrays.asList(args).contains(var)) {
        fileWriter.write("    int " + var + "\n");
      }
    }
    for (Command command : commands) {
      fileWriter.write("    ".repeat(depth));
      command.cpp(fileWriter, comments);
      fileWriter.write("\n");
    }
    fileWriter.write("}\n\n");
  }
}

class WhileBlock extends Block {
  public Variable variable;
  public Block parent;

  public WhileBlock(Variable Variable, int LineNumber, Block Parent) {
    super(LineNumber, -1);
    variable = Variable;
    parent = Parent;
  }

  @Override
  public List<Variable> GetAllVariables() {
    List<Variable> vars = new ArrayList<>(variables.values());
    vars.addAll(parent.GetAllVariables());
    return vars;
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
  int getDepth() {
    return parent.getDepth() + 1;
  }

  @Override
  void run(HashMap<Integer, Boolean> breakpoints, Block _group) throws BareBonesException {
    while (variable.data != 0) {
      debug(breakpoints, this);
      for (Command command : commands) {
        command.run(breakpoints, this);
      }
    }
  }

  @Override
  void format(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("while " + variable.name + " not 0 do;");
    comment(fileWriter, comments);
    fileWriter.write("\n");
    for (Command command : commands) {
      fileWriter.write("    ".repeat(getDepth()));
      command.format(fileWriter, comments);
      fileWriter.write("\n");
    }
    fileWriter.write("    ".repeat(getDepth() - 1) + "end;");
    int endLineNum = commands.get(commands.size() - 1).lineNumber + 1;
    if (comments.get(endLineNum) != null) {
      fileWriter.write(" //" + comments.get(endLineNum));
    }
  }

  @Override
  void py(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("while " + variable.name + " != 0:");
    pyComment(fileWriter, comments);
    fileWriter.write("\n");
    int i = 0;
    for (Command command : commands) {
      i += 1;
      fileWriter.write("    ".repeat(getDepth()));
      command.py(fileWriter, comments);
      if (i != commands.size()) {
        fileWriter.write("\n");
      }
    }
    int endLineNum = commands.get(commands.size() - 1).lineNumber + 1;
    if (comments.get(endLineNum) != null) {
      fileWriter.write("\n" + "    ".repeat(getDepth()) + "#" + comments.get(endLineNum));
    }
  }

  @Override
  void rust(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("while " + variable.name + " != 0 {");
    for (String var : variables.keySet()) {
      fileWriter.write("\n" + "    ".repeat(getDepth()) + "let mut " + var + ": i32;");
    }
    comment(fileWriter, comments);
    fileWriter.write("\n");
    for (Command command : commands) {
      fileWriter.write("    ".repeat(getDepth()));
      command.rust(fileWriter, comments);
      fileWriter.write("\n");
    }
    int endLineNum = commands.get(commands.size() - 1).lineNumber + 1;
    fileWriter.write("    ".repeat(getDepth() - 1) + "}");
    if (comments.get(endLineNum) != null) {
      fileWriter.write(" //" + comments.get(endLineNum));
    }
  }

  @Override
  void cpp(FileWriter fileWriter, HashMap<Integer, String> comments) throws IOException {
    fileWriter.write("while (" + variable.name + " != 0) {");
    comment(fileWriter, comments);
    fileWriter.write("\n");
    for (String var : variables.keySet()) {
      fileWriter.write("    ".repeat(getDepth()) + "int " + var + ";\n");
    }
    for (Command command : commands) {
      fileWriter.write("    ".repeat(getDepth()));
      command.cpp(fileWriter, comments);
      fileWriter.write("\n");
    }
    int endLineNum = commands.get(commands.size() - 1).lineNumber + 1;
    fileWriter.write("    ".repeat(getDepth() - 1) + "}");
    if (comments.get(endLineNum) != null) {
      fileWriter.write(" //" + comments.get(endLineNum));
    }
  }
}
