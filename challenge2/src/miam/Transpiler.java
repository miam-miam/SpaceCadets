package miam;

import java.io.FileWriter;
import java.io.IOException;
import miam.Command.CommandType;

public class Transpiler {
  public static void Format(Parser parser, String file) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    String indent = "";
    String comment;
    int LineNum = 0;
    for (Command command : parser.Instructions) {
      LineNum += 1;
      switch (command.Type) {
        case INCR:
          fileWriter.write(indent + "incr " + parser.Vars.get(command.Id) + ";");
          break;
        case DECR:
          fileWriter.write(indent + "decr " + parser.Vars.get(command.Id) + ";");
          break;
        case CLEAR:
          fileWriter.write(indent + "clear " + parser.Vars.get(command.Id) + ";");
          break;
        case WHILE:
          fileWriter.write(
              indent
                  + "while "
                  + parser.Vars.get(parser.Loops.get(command.Id).Variable)
                  + " not 0 do;");
          indent += "    ";
          // Not using string builder as there are relatively few while loops and it
          // would also require us to create a new instance whenever end is called.
          break;
        case END:
          indent = indent.substring(4);
          fileWriter.write(indent + "end;");
          break;
      }
      if ((comment = parser.Comments.get(LineNum)) != null && !comment.trim().equals("")) {
        fileWriter.write(" //" + comment + "\n");
      } else {
        fileWriter.write("\n");
      }
    }
    fileWriter.close();
  }

  public static void Pi(Parser parser, String file) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    String indent = "";
    String comment;
    int LineNum = 0;
    for (Command command : parser.Instructions) {
      LineNum += 1;
      switch (command.Type) {
        case INCR:
          fileWriter.write(indent + parser.Vars.get(command.Id) + " += 1");
          break;
        case DECR:
          fileWriter.write(indent + parser.Vars.get(command.Id) + " -= 1");
          break;
        case CLEAR:
          fileWriter.write(indent + parser.Vars.get(command.Id) + " = 0");
          break;
        case WHILE:
          fileWriter.write(
              indent
                  + "while "
                  + parser.Vars.get(parser.Loops.get(command.Id).Variable)
                  + " != 0:");
          indent += "    ";
          // Not using string builder as there are relatively few while loops and it
          // would also require us to create a new instance whenever end is called.
          break;
        case END:
          indent = indent.substring(4);
          break;
      }
      if ((comment = parser.Comments.get(LineNum)) != null && !comment.trim().equals("")) {
        fileWriter.write(" #" + comment + "\n");
      } else if (command.Type != CommandType.END) {
        fileWriter.write("\n");
      }
    }
    fileWriter.close();
  }

  public static void Java(Parser parser, String file) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    String indent = "    ";
    String comment;
    int LineNum = 0;
    fileWriter.write(
        "public class Main {\n" + "\n" + "  public static void main(String[] args) {\n");
    for (String var : parser.Vars) {
      fileWriter.write(indent + "Integer " + var + ";\n");
    }
    for (Command command : parser.Instructions) {
      LineNum += 1;
      switch (command.Type) {
        case INCR:
          fileWriter.write(indent + parser.Vars.get(command.Id) + " += 1;");
          break;
        case DECR:
          fileWriter.write(indent + parser.Vars.get(command.Id) + " -= 1;");
          break;
        case CLEAR:
          fileWriter.write(indent + parser.Vars.get(command.Id) + " = 0;");
          break;
        case WHILE:
          fileWriter.write(
              indent
                  + "while ("
                  + parser.Vars.get(parser.Loops.get(command.Id).Variable)
                  + " != 0) {");
          indent += "  ";
          // Not using string builder as there are relatively few while loops and it
          // would also require us to create a new instance whenever end is called.
          break;
        case END:
          indent = indent.substring(2);
          fileWriter.write(indent + "}");
          break;
      }
      if ((comment = parser.Comments.get(LineNum)) != null && !comment.trim().equals("")) {
        fileWriter.write(" //" + comment + "\n");
      } else {
        fileWriter.write("\n");
      }
    }
    fileWriter.write("  }\n" + "}");
    fileWriter.close();
  }

  public static void Rust(Parser parser, String file) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    String indent = "    ";
    String comment;
    int LineNum = 0;
    fileWriter.write("fn main() {\n");
    for (String var : parser.Vars) {
      fileWriter.write(indent + "let mut " + var + ":i32;\n");
    }
    for (Command command : parser.Instructions) {
      LineNum += 1;
      switch (command.Type) {
        case INCR:
          fileWriter.write(indent + parser.Vars.get(command.Id) + " += 1;");
          break;
        case DECR:
          fileWriter.write(indent + parser.Vars.get(command.Id) + " -= 1;");
          break;
        case CLEAR:
          fileWriter.write(indent + parser.Vars.get(command.Id) + " = 0;");
          break;
        case WHILE:
          fileWriter.write(
              indent
                  + "while "
                  + parser.Vars.get(parser.Loops.get(command.Id).Variable)
                  + " != 0 {");
          indent += "    ";
          // Not using string builder as there are relatively few while loops and it
          // would also require us to create a new instance whenever end is called.
          break;
        case END:
          indent = indent.substring(4);
          fileWriter.write(indent + "}");
          break;
      }
      if ((comment = parser.Comments.get(LineNum)) != null && !comment.trim().equals("")) {
        fileWriter.write(" //" + comment + "\n");
      } else {
        fileWriter.write("\n");
      }
    }
    fileWriter.write("}");
    fileWriter.close();
  }
}
