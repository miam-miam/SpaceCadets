package miam;

import java.io.IOException;

public class Main {

  public static void main(String[] args) throws BareBonesException {
    Parser parser = new Parser(args[0]);
    try {
      Formatter.Format(parser, "bareBones/format.bb");
    } catch (IOException e) {
      throw new BareBonesException("Could not write formatted file.");
    }
  }
}
