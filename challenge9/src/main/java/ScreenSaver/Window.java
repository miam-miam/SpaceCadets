package ScreenSaver;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/** Window listener that closes the screensaver if the user tries to minimise it. */
public class Window implements WindowListener {

  @Override
  public void windowOpened(WindowEvent e) {}

  @Override
  public void windowClosing(WindowEvent e) {
    ScreenSaver.active = false;
    e.getWindow().dispose();
  }

  @Override
  public void windowClosed(WindowEvent e) {}

  @Override
  public void windowIconified(WindowEvent e) {}

  @Override
  public void windowDeiconified(WindowEvent e) {

    ScreenSaver.active = false;
    e.getWindow().dispose();
  }

  @Override
  public void windowActivated(WindowEvent e) {}

  @Override
  public void windowDeactivated(WindowEvent e) {
    ScreenSaver.active = false;
    e.getWindow().dispose();
  }
}
