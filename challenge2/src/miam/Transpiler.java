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
    parser.Group.py(fileWriter, parser.Comments);
    fileWriter.close();
  }

  //  public static void java(Parser parser, String file) throws IOException {
  //    FileWriter fileWriter = new FileWriter(file);
  //    fileWriter.write(
  //        "public class Main {\n" + "\n" + "  public static void main(String[] args) {\n");
  //    for (String var : parser.Variables.keySet()) {
  //      fileWriter.write("    Integer " + var + ";\n");
  //    }
  //    parser.Group.java(fileWriter, parser.Comments);
  //    fileWriter.write("  }\n" + "}");
  //    fileWriter.close();
  //  }
  //
  //  public static void rust(Parser parser, String file) throws IOException {
  //    FileWriter fileWriter = new FileWriter(file);
  //    fileWriter.write("fn main() {\n");
  //    for (String var : parser.Variables.keySet()) {
  //      fileWriter.write("    let mut " + var + ": i32;\n");
  //    }
  //    parser.Group.rust(fileWriter, parser.Comments);
  //    fileWriter.write("}");
  //    fileWriter.close();
  //  }
  //
  //  public static void cpp(Parser parser, String file) throws IOException {
  //    FileWriter fileWriter = new FileWriter(file);
  //    fileWriter.write("int main() {\n");
  //    for (String var : parser.Variables.keySet()) {
  //      fileWriter.write("    int " + var + ";\n");
  //    }
  //    parser.Group.cpp(fileWriter, parser.Comments);
  //    fileWriter.write("}");
  //    fileWriter.close();
  //  }
}
