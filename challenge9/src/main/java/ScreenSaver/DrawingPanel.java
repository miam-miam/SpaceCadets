package ScreenSaver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.swing.JPanel;

/** A Drawing panel that contains a space for snowflakes to be drawn to */
public class DrawingPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private static final Random random = new Random();
  private List<Snowflake> snowflakes;
  private int counter = 0;

  public DrawingPanel(Dimension drawingPanelSize) {
    this.setPreferredSize(drawingPanelSize);
    this.snowflakes = new ArrayList<>();
  }

  /**
   * Add a Snowflake to the list. The Exception could be thrown if the images have yet to load, in
   * which case we shouldn't do anything.
   */
  public void addSnowflake() {
    try {
      snowflakes.add(new Snowflake(getWidth(), getHeight()));
    } catch (IOException ignored) {
    }
  }

  /** Paint the snowflakes to the screen. @param g the Graphics to paint on. */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    final int delta = 1;
    counter += 1;
    if (counter > 10 + random.nextInt(120)) {
      counter = 0;
      addSnowflake();
    }
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());
    g.setColor(Color.WHITE);
    for (Snowflake snowflake : snowflakes) {
      g.drawImage(snowflake.image, snowflake.x, snowflake.y, this);
    }
    // Advance all snowflakes and remove snowflakes that are no longer on the screen.
    snowflakes =
        snowflakes.stream()
            .filter(snowflake -> !snowflake.advance(delta))
            .collect(Collectors.toList());
  }
}
