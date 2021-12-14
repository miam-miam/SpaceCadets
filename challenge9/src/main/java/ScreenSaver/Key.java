package ScreenSaver;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

/** Close ScreenSaver if a key is pressed. */
public class Key implements KeyListener {
  private final JFrame screenSaverFrame;

  public Key(JFrame screenSaverFrame) {
    this.screenSaverFrame = screenSaverFrame;
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    ScreenSaver.active = false;
    screenSaverFrame.dispose();
  }

  @Override
  public void keyReleased(KeyEvent e) {}
}
