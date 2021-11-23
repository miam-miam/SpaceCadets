import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import java.awt.Component;

/**
 * An animation that is run on a separate thread whenever an image from the webcam is obtained.
 */
public class Animation implements Runnable, WebcamListener {

  final Component[] outputComponents;
  final CircleDetector circleDetector;

  Animation(Component[] outputComponent, CircleDetector circleDetector) {
    this.outputComponents = outputComponent;
    this.circleDetector = circleDetector;
  }

  @Override
  public void webcamOpen(WebcamEvent we) {
  }

  @Override
  public void webcamClosed(WebcamEvent we) {
  }

  @Override
  public void webcamDisposed(WebcamEvent we) {
  }

  /**
   * @param we The event that caused the function to be called.
   */
  @Override
  public void webcamImageObtained(WebcamEvent we) {
    long start = System.currentTimeMillis();
    // Copy Image into inputImage as we.getImage() is in an unknown format.
    circleDetector.inputImage.getGraphics().drawImage(we.getImage(), 0, 0, null);
    circleDetector.sobelImage();
    int index = circleDetector.houghImage();
    circleDetector.drawTarget(index);
    for (Component component : outputComponents) {
      component.repaint();
    }
    long end = System.currentTimeMillis();
    System.out.println(
        "Took "
            + (end - start)
            + " ms to draw frame."); // Have 50 ms to do everything has my webcam works at 20 fps.
  }

  /**
   * Add itself to the webcam listeners so that it can act when an image is obtained.
   */
  @Override
  public void run() {
    circleDetector.webcam.addWebcamListener(this);
  }
}
