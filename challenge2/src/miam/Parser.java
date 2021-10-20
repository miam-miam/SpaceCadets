package miam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miam.Command.CommandType;

// At first tried making a recursive regex parser but then learnt that matched groups inside
// recursive patterns are inaccessible :(
// (/\s*(?:(?:incr\s+(\w)\s*;)|(?:decr\s{1,}(\w)\s*;)|(?:clear\s+(\w)\s*;)|(?:while\s+(\w)\s+not\s+0\s+do\s*;(?R)*\s*(end)\s*;))/gA)

public class Parser {
  public static final Pattern PATTERN =
      Pattern.compile(
          "(?:incr\\s+(\\w)|decr\\s+(\\w)|clear\\s+(\\w)|while\\s+(\\w)\\s+not\\s+0\\s+do|(end))\\s*;\\s*|\\s*(//.*)");
  public List<Command> Instructions = new LinkedList<>();
  public Stack<Integer> OpenLoops = new Stack<>();
  public List<Loop> Loops = new LinkedList<>();
  public List<String> Vars = new LinkedList<>();

  public Parser(String file) throws BareBonesException {
    File code = new File(file);
    if (!code.exists()) {
      throw new BareBonesException("Could not find file");
    }
    try {
      FileReader fileReader = new FileReader(code);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      StringBuilder stringFile = new StringBuilder();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        try {
          Matcher matcher = PATTERN.matcher(line);
          System.out.println("line: " + line);
          System.out.println("looking: " + matcher.find(0));
          System.out.println(matcher);
          AddCommand(matcher);
          while (!matcher.hitEnd()) {
            // More than one command on a single line.
            System.out.println("matching: " + matcher.find(matcher.end()));
            System.out.println(matcher);
            AddCommand(matcher);
          }
        } catch (BareBonesException e) {
          throw new BareBonesException(e.getMessage() + "On this line: " + line);
        }
      }
      fileReader.close();
    } catch (FileNotFoundException e) {
      throw new BareBonesException("Could not find file: " + e.getMessage());
    } catch (IOException e) {
      throw new BareBonesException("Could not close file: " + e.getMessage());
    }
  }

  public void AddCommand(Matcher match) throws BareBonesException {
    String res;
    int varIndex;
    System.out.println(match.toMatchResult());
    if ((res = match.group(1)) != null) {
      if ((varIndex = Vars.indexOf(res)) == -1) {
        throw new BareBonesException("Variable used before it is instantiated.");
      }
      Instructions.add(new Command(CommandType.INCR, varIndex));
    } else if ((res = match.group(2)) != null) {
      if ((varIndex = Vars.indexOf(res)) == -1) {
        throw new BareBonesException("Variable used before it is instantiated.");
      }
      Instructions.add(new Command(CommandType.DECR, varIndex));
    } else if ((res = match.group(3)) != null) {
      if (!Vars.contains(res)) {
        Vars.add(res);
        varIndex = Vars.size() - 1;
      } else {
        varIndex = Vars.indexOf(res);
      }
      Instructions.add(new Command(CommandType.CLEAR, varIndex));
    } else if ((res = match.group(4)) != null) {
      if ((varIndex = Vars.indexOf(res)) == -1) {
        throw new BareBonesException("Variable used before it is instantiated.");
      }
      Loops.add(new Loop(varIndex, Instructions.size()));
      OpenLoops.push(Loops.size() - 1);
      Instructions.add(new Command(CommandType.WHILE, Loops.size() - 1));
    } else if (match.group(5) != null) {
      try {
        Instructions.add(new Command(CommandType.END, OpenLoops.pop()));
      } catch (EmptyStackException e) {
        throw new BareBonesException("Unexpected \"end;\".");
      }
    } else if (match.group(6) != null) {
      // This is where comments are matched.
    }
  }
}
