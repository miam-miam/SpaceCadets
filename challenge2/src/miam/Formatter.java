package miam;

import java.io.FileWriter;
import java.io.IOException;

public class Formatter {
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
          fileWriter.write(indent + "while " + parser.Vars.get(command.Id) + " not 0 do;");
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
}
