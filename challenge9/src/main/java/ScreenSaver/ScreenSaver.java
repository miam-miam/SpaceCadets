package ScreenSaver;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class ScreenSaver extends WindowAdapter implements Runnable {
  static boolean active = false;

  public static boolean isActive() {
    return active;
  }

  @Override
  public void run() {
    active = true;
    final JFrame screenSaverFrame = new JFrame();
    screenSaverFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    screenSaverFrame.setUndecorated(true);
    screenSaverFrame.setResizable(false);
    screenSaverFrame.add(
        new JLabel("This is a Java Screensaver!", SwingConstants.CENTER), BorderLayout.CENTER);
    screenSaverFrame.addMouseMotionListener(new MouseMotion(screenSaverFrame));
    screenSaverFrame.addWindowListener(new Window());
    screenSaverFrame.addWindowListener(this);
    screenSaverFrame.validate();
    GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .setFullScreenWindow(screenSaverFrame);
  }
}
