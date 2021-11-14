import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import javax.imageio.ImageIO;

public class BMPReader extends Window {
  final BufferedImage img;

  public BMPReader(String file) throws IOException {
    img = ImageIO.read(new File(file));
    smallestX = 0;
    largestX = img.getWidth();
  }

  public TreeMap<Coordinate, Character> generate() {
    TreeMap<Coordinate, Character> outputs = new TreeMap<>();
    int height = img.getHeight();
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < largestX; w++) {
        if (img.getRGB(w, h) != 0xFFFFFFFF) {
          outputs.put(new Coordinate(w, h, largestX), 'O');
        }
      }
    }
    return outputs;
  }
}
