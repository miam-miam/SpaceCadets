package miam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// At first tried making a recursive regex parser but then learnt that matched groups inside
// recursive patterns are inaccessible :(
// (/\s*(?:(?:incr\s+(\w)\s*;)|(?:decr\s{1,}(\w)\s*;)|(?:clear\s+(\w)\s*;)|(?:while\s+(\w)\s+not\s+0\s+do\s*;(?R)*\s*(end)\s*;))/gA)

public class Parser {
  private static final Pattern PATTERN =
      Pattern.compile(
          "(?:incr\\s+(\\w)|decr\\s+(\\w)|clear\\s+(\\w)|while\\s+(\\w)\\s+not\\s+0\\s+do|(end))\\s*;\\s*|\\s*//(.*)");
  private final Stack<Block> Groups = new Stack<>();
  public HashMap<String, Variable> Variables = new HashMap<>();
  public HashMap<Integer, String> Comments = new HashMap<>();
  public Block Group;
  private int lineNumber = 1;

  public Parser(String file) throws BareBonesException {
    File code = new File(file);
    if (!code.exists()) {
      throw new BareBonesException("Could not find file");
    }
    try {
      FileReader fileReader = new FileReader(code);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String line;
      Groups.push(new Block(lineNumber, 0));
      while ((line = bufferedReader.readLine()) != null) {
        try {
          Matcher matcher = PATTERN.matcher(line);
          if (!matcher.find(0)) {
            throw new BareBonesException("Unexpected token.");
          }
          AddCommand(matcher);
          lineNumber += 1;
          while (!matcher.hitEnd()) {
            // More than one command on a single line.
            if (!matcher.find(matcher.end())) {
              throw new BareBonesException("Unexpected token.");
            }
            AddCommand(matcher);
            lineNumber += 1;
          }
        } catch (BareBonesException e) {
          fileReader.close(); // Need to make sure to close the file as throw returns from func
          throw new BareBonesException(e.getMessage() + " On this line: " + line);
        }
      }
      fileReader.close();
      if (Groups.size() != 1) {
        throw new BareBonesException("Did not end open code blocks.");
      }
      Group = Groups.pop();
    } catch (FileNotFoundException e) {
      throw new BareBonesException("Could not find file: " + e.getMessage());
    } catch (IOException e) {
      throw new BareBonesException("Could not close file: " + e.getMessage());
    }
  }

  private void AddCommand(Matcher match) throws BareBonesException {
    String res;
    Variable var;
    if ((res = match.group(1)) != null) {
      if ((var = Variables.get(res)) == null) {
        throw new BareBonesException("Variable " + res + " is used before it is instantiated.");
      }
      Groups.lastElement().add(new Incr(var, lineNumber));
    } else if ((res = match.group(2)) != null) {
      if ((var = Variables.get(res)) == null) {
        throw new BareBonesException("Variable " + res + " is used before it is instantiated.");
      }
      Groups.lastElement().add(new Decr(var, lineNumber));
    } else if ((res = match.group(3)) != null) {
      var = Variables.get(res);
      if (var == null) {
        var = new Variable(res);
        Variables.put(res, var);
      }
      Groups.lastElement().add(new Clear(var, lineNumber));
    } else if ((res = match.group(4)) != null) {
      if ((var = Variables.get(res)) == null) {
        throw new BareBonesException("Variable " + res + " is used before it is instantiated.");
      }
      Groups.push(new WhileBlock(var, lineNumber, Groups.lastElement().depth + 1));
    } else if (match.group(5) != null) {
      try {
        Block group = Groups.pop();
        if (group instanceof WhileBlock) {
          Groups.lastElement().add(group);
        } else {
          throw new BareBonesException("Unexpected \"end;\".");
        }
      } catch (EmptyStackException e) {
        throw new BareBonesException("Unexpected \"end;\".");
      }
    } else if (match.group(6) != null) {
      // This is where comments are matched.
      lineNumber -= 1;
      if (!match.group(6).trim().equals("")) {
        Comments.merge(lineNumber, match.group(6), String::concat);
      }
    }
  }
}
