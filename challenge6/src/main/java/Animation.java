import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import java.awt.Component;

public class Animation implements Runnable, WebcamListener {
  final Component[] outputComponents;
  final JOCLSimpleImage joclSimpleImage;

  Animation(Component[] outputComponent, JOCLSimpleImage joclSimpleImage) {
    this.outputComponents = outputComponent;
    this.joclSimpleImage = joclSimpleImage;
  }

  @Override
  public void webcamOpen(WebcamEvent we) {}

  @Override
  public void webcamClosed(WebcamEvent we) {}

  @Override
  public void webcamDisposed(WebcamEvent we) {}

  @Override
  public void webcamImageObtained(WebcamEvent we) {
    long start = System.currentTimeMillis();
    joclSimpleImage.inputImage.getGraphics().drawImage(we.getImage(), 0, 0, null);
    joclSimpleImage.sobelImage();
    int index = joclSimpleImage.houghImage();
    joclSimpleImage.drawTarget(index);
    for (Component component : outputComponents) {
      component.repaint();
    }
    long end = System.currentTimeMillis();
    System.out.println(
        "Took "
            + (end - start)
            + " ms to draw frame."); // Have 50 ms to do everything has my webcam works at 20 fps.
  }

  @Override
  public void run() {
    joclSimpleImage.webcam.addWebcamListener(this);
  }
}
