package miam;

import java.io.FileWriter;
import java.io.IOException;

/**
 * The Transpiler converts a parsed file into a .bb (bareBones file), .py, .java, .rs or .cpp. The
 * transpiler cannot promise that the code produced will give the exact same output as the
 * interpreter due to differences in how each language deals with stuff like overflows.
 */
public class Transpiler {
  public static void format(Parser parser, String file) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    for (FuncBlock f : parser.Functions.values()) {
      f.format(fileWriter, parser.Comments);
    }
    parser.Group.format(fileWriter, parser.Comments);
    fileWriter.close();
  }

  public static void py(Parser parser, String file) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    for (FuncBlock f : parser.Functions.values()) {
      f.py(fileWriter, parser.Comments);
    }
    parser.Group.py(fileWriter, parser.Comments);
    fileWriter.close();
  }

  public static void rust(Parser parser, String file) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    for (FuncBlock funcs : parser.Functions.values()) {
      funcs.rust(fileWriter, parser.Comments);
    }
    fileWriter.write("fn main() {\n");
    parser.Group.rust(fileWriter, parser.Comments);
    fileWriter.write("}");
    fileWriter.close();
  }

  public static void cpp(Parser parser, String file) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    for (FuncBlock funcs : parser.Functions.values()) {
      funcs.cpp(fileWriter, parser.Comments);
    }
    fileWriter.write("int main() {\n");
    parser.Group.cpp(fileWriter, parser.Comments);
    fileWriter.write("}");
    fileWriter.close();
  }
}
