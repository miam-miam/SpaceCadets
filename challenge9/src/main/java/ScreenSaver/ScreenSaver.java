package ScreenSaver;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public class ScreenSaver implements Runnable {
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
    screenSaverFrame.getContentPane().setBackground(Color.BLACK);
    screenSaverFrame.addMouseMotionListener(new MouseMotion(screenSaverFrame));
    screenSaverFrame.addKeyListener(new Key(screenSaverFrame));
    screenSaverFrame.addWindowListener(new Window());
    GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .setFullScreenWindow(screenSaverFrame);
    DrawingPanel panel = new DrawingPanel(screenSaverFrame.getSize());
    screenSaverFrame.getContentPane().add(panel);
    screenSaverFrame.validate();

    // Repaint panel every 16 ms (about 60 fps)
    Timer timer = new Timer(16, (ActionEvent e) -> panel.repaint());
    timer.start();
  }
}
