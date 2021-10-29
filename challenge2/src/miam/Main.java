package miam;

import java.io.IOException;
import java.util.HashMap;

public class Main {

  /**
   * @param args First argument used to find what file to parse. Second argument used in the
   *     following ways: No arguments - File ran normally Files ending in .py, .bb, .java, .rs, .cpp
   *     - Transpiled into their corresponding formats and stored in the file given. Anything else -
   *     File ran with debugger
   * @throws BareBonesException Throws a custom exception that can describe errors during parsing or
   *     interpreting.
   */
  public static void main(String[] args) throws BareBonesException {
    Parser parser = new Parser(args[0]);
    if (args[1] != null) {
      if (args[1].endsWith(".py")) {
        try {
          Transpiler.py(parser, args[1]);
        } catch (IOException e) {
          throw new BareBonesException("Could not write formatted file.");
        }
      } else if (args[1].endsWith(".bb")) {
        try {
          Transpiler.format(parser, args[1]);
        } catch (IOException e) {
          throw new BareBonesException("Could not write formatted file.");
        }
        //      } else if (args[1].endsWith(".java")) {
        //        try {
        //          Transpiler.java(parser, args[1]);
        //        } catch (IOException e) {
        //          throw new BareBonesException("Could not write formatted file.");
        //        }
        //      } else if (args[1].endsWith(".rs")) {
        //        try {
        //          Transpiler.rust(parser, args[1]);
        //        } catch (IOException e) {
        //          throw new BareBonesException("Could not write formatted file.");
        //        }
        //      } else if (args[1].endsWith(".cpp")) {
        //        try {
        //          Transpiler.cpp(parser, args[1]);
        //        } catch (IOException e) {
        //          throw new BareBonesException("Could not write formatted file.");
        //        }
      } else {
        Interpreter interpreter = new Interpreter(parser);
        HashMap<Integer, Boolean> map = new HashMap<>();
        map.put(1, true);
        interpreter.start(map);
      }
    } else {
      Interpreter interpreter = new Interpreter(parser);
      interpreter.start();
    }
  }
}
