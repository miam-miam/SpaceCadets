import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import javax.imageio.ImageIO;

class BMPReader extends Window {
  final BufferedImage img;

  /**
   * Creates a BitMap Reader that can then generate a set of points to be printed.
   *
   * @param file the file from which the bmp should be read from.
   * @throws IOException If the image cannot be read.
   */
  public BMPReader(String file) throws IOException {
    img = ImageIO.read(new File(file));
    smallestX = 0;
    largestX = img.getWidth();
  }

  /** @return The set of points where the image is black. */
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
