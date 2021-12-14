package ScreenSaver;

import java.awt.event.MouseEvent;
import javax.swing.JFrame;

/** Close ScreenSaver if the mouse moves. */
public class MouseMotion implements java.awt.event.MouseMotionListener {

  private final JFrame screenSaverFrame;
  boolean first;

  public MouseMotion(JFrame screenSaverFrame) {
    this.screenSaverFrame = screenSaverFrame;
    first = true;
  }

  @Override
  public void mouseDragged(MouseEvent e) {}

  @Override
  public void mouseMoved(MouseEvent e) {
    if (first) {
      first = false;
    } else {
      ScreenSaver.active = false;
      screenSaverFrame.dispose();
    }
  }
}
