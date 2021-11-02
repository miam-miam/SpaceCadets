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

/**
 * The parser parses a BareBones source code into commands that can then be used by the transpiler
 * or interpreter. A stack is used to separate code blocks into smaller chunks. This could then be
 * used at a later date as a way of adding scoped variables.
 */
public class Parser {
  private static final Pattern PATTERN =
      Pattern.compile(
          "\\s*(?:incr\\s+(\\w+)|decr\\s+(\\w+)|clear\\s+(\\w+)|while\\s+(\\w+)\\s+not\\s+0\\s+do|(end)|func\\s+(\\w+)\\s*\\((\\s*\\w+\\s*(?:,\\s*\\w+\\s*)*)\\)|(\\w+)\\s*\\((\\s*[&]?\\w+\\s*(?:,\\s*[&]?\\w+\\s*)*)\\))\\s*;\\s*|\\s*//[ \\t]*+(.+)?[ \\t]*\\n\\s*"); // Pattern to find the 8 different commands in a BareBones file (counting comments)
  private final Stack<Block> Groups = new Stack<>();
  public HashMap<Integer, String> Comments = new HashMap<>();
  public HashMap<String, FuncBlock> Functions = new HashMap<>();
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
      Groups.push(new Block(lineNumber, 1));
      while ((line = bufferedReader.readLine()) != null) {
        try {
          if (!line.equals("")) {
            Matcher matcher = PATTERN.matcher(line);
            if (!matcher.find(0) || matcher.start() != 0) {
              throw new BareBonesException("Unexpected token.");
            }
            AddCommand(matcher);
            while (!matcher.hitEnd()) {
              // More than one command on a single line.
              int end = matcher.end();
              if (!matcher.find(matcher.end()) || matcher.start() != end) {
                throw new BareBonesException("Unexpected token.");
              }
              AddCommand(matcher);
            }
          }
          lineNumber += 1;
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

  private Variable FindVariable(String term) {
    for (int i = Groups.size() - 1; i >= 0; i--) {
      Variable variable = Groups.get(i).variables.get(term);
      if (variable != null) {
        return variable;
      }
    }
    return null;
  }

  private void AddCommand(Matcher match) throws BareBonesException {
    String res;
    Variable var;
    if ((res = match.group(1)) != null) {
      if ((var = FindVariable(res)) == null) {
        throw new BareBonesException("Variable " + res + " is used before it is instantiated.");
      }
      Groups.lastElement().add(new Incr(var, lineNumber));
    } else if ((res = match.group(2)) != null) {
      if ((var = FindVariable(res)) == null) {
        throw new BareBonesException("Variable " + res + " is used before it is instantiated.");
      }
      Groups.lastElement().add(new Decr(var, lineNumber));
    } else if ((res = match.group(3)) != null) {
      var = FindVariable(res);
      if (var == null) {
        var = new Variable(res);
        Groups.lastElement().variables.put(res, var);
      }
      Groups.lastElement().add(new Clear(var, lineNumber));
    } else if ((res = match.group(4)) != null) {
      if ((var = FindVariable(res)) == null) {
        throw new BareBonesException("Variable " + res + " is used before it is instantiated.");
      }
      Groups.push(
          new WhileBlock(var, lineNumber, Groups.lastElement().depth + 1, Groups.lastElement()));
    } else if (match.group(5) != null) {
      try {
        Block group = Groups.pop();
        if (group instanceof WhileBlock) {
          Groups.lastElement().add(group);
        } else if (!(group instanceof FuncBlock)) {
          throw new BareBonesException("Unexpected \"end;\".");
        }
      } catch (EmptyStackException e) {
        throw new BareBonesException("Unexpected \"end;\".");
      }
    } else if (match.group(6) != null && match.group(7) != null) {
      String func_name = match.group(6);
      String[] args = match.group(7).split("\\s*,\\s*");
      if (Groups.lastElement() instanceof WhileBlock | Groups.lastElement() instanceof FuncBlock) {
        throw new BareBonesException("Functions cannot be defined in a non-global scope.");
      }
      FuncBlock func = new FuncBlock(args, lineNumber, func_name);
      Functions.put(func_name, func);
      Groups.push(func);
    } else if (match.group(8) != null && match.group(9) != null) {
      String func_name = match.group(8);
      String[] args = match.group(9).split("\\s*,\\s*");
      FuncBlock func = Functions.get(func_name);
      if (func == null) {
        throw new BareBonesException("Could not find function.");
      } else if (func.args.length != args.length) {
        throw new BareBonesException("Function has incorrect argument count.");
      } else {
        boolean[] references = new boolean[args.length];
        Variable[] vars = new Variable[args.length];
        for (int i = 0; i < args.length; i++) {
          String sanitisedArg;
          if (args[i].startsWith("&")) {
            sanitisedArg = args[i].substring(1);
            references[i] = true;
          } else {
            sanitisedArg = args[i];
          }
          if ((var = FindVariable(sanitisedArg)) == null) {
            throw new BareBonesException(
                "Variable " + sanitisedArg + " is used before it is instantiated.");
          }
          vars[i] = var;
        }
        Groups.lastElement().add(new Func(vars, func, references, lineNumber));
      }
    } else if (match.group(10) != null) {
      // This is where comments are matched.
      lineNumber -= 1;
      Comments.merge(lineNumber, match.group(10), String::concat);
    }
  }
}
