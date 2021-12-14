package ScreenSaver;

import java.awt.Image;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import javax.imageio.ImageIO;

public class Snowflake {
  private static final Random random = new Random();
  private static Image[] images;

  static {
    try {
      images =
          new Image[] {
            ImageIO.read(
                Objects.requireNonNull(
                    Snowflake.class.getClassLoader().getResource("snowflake1.png"))),
            ImageIO.read(
                Objects.requireNonNull(
                    Snowflake.class.getClassLoader().getResource("snowflake2.png"))),
            ImageIO.read(
                Objects.requireNonNull(
                    Snowflake.class.getClassLoader().getResource("snowflake3.png"))),
            ImageIO.read(
                Objects.requireNonNull(
                    Snowflake.class.getClassLoader().getResource("snowflake4.png"))),
            ImageIO.read(
                Objects.requireNonNull(
                    Snowflake.class.getClassLoader().getResource("snowflake5.png"))),
            ImageIO.read(
                Objects.requireNonNull(
                    Snowflake.class.getClassLoader().getResource("snowflake6.png"))),
            ImageIO.read(
                Objects.requireNonNull(
                    Snowflake.class.getClassLoader().getResource("snowflake7.png"))),
            ImageIO.read(
                Objects.requireNonNull(
                    Snowflake.class.getClassLoader().getResource("snowflake8.png"))),
            ImageIO.read(
                Objects.requireNonNull(
                    Snowflake.class.getClassLoader().getResource("snowflake9.png"))),
          };
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private final int height;
  private final int speed = random.nextInt(2);
  public Image image;
  public int x;
  public int y;
  private final int imageHeight;

  /**
   * Create a new Snowflake at a random x position.
   *
   * @param width The width of the drawing panel
   * @param height The height of the drawing panel
   * @throws IOException if the image of the snowflake cannot be read.
   */
  public Snowflake(int width, int height) throws IOException {
    image = images[random.nextInt(images.length)];
    if (image == null) {
      throw new IOException("Could not fetch image");
    }
    int imageWidth = image.getWidth(null);
    imageHeight = image.getHeight(null);
    x = random.nextInt(width + imageWidth) - imageWidth - 1;
    y = -imageHeight;
    this.height = height;
  }

  /**
   * Advance the snowflake by a certain amount and speed.
   *
   * @param amount the amount to advance the snowflake by.
   * @return whether the snowflake is still on the screen.
   */
  public boolean advance(int amount) {
    y += amount + speed;
    return y > (height + imageHeight);
  }
}
