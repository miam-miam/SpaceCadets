package miam;

import java.io.IOException;
import java.util.HashMap;

public class Main {

  public static void main(String[] args) throws BareBonesException {
    Parser parser = new Parser(args[0]);
    try {
      Transpiler.py(parser, "bareBones/main.py");
    } catch (IOException e) {
      throw new BareBonesException("Could not write formatted file.");
    }
    try {
      Transpiler.format(parser, "bareBones/format.bb");
    } catch (IOException e) {
      throw new BareBonesException("Could not write formatted file.");
    }
    try {
      Transpiler.java(parser, "bareBones/Main.java");
    } catch (IOException e) {
      throw new BareBonesException("Could not write formatted file.");
    }
    try {
      Transpiler.rust(parser, "bareBones/main.rs");
    } catch (IOException e) {
      throw new BareBonesException("Could not write formatted file.");
    }
    try {
      Transpiler.cpp(parser, "bareBones/main.cpp");
    } catch (IOException e) {
      throw new BareBonesException("Could not write formatted file.");
    }
    Interpreter interpreter = new Interpreter(parser);
    HashMap<Integer, Boolean> map = new HashMap<>();
    map.put(1, true);
    interpreter.start(map);
  }
}
