import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import java.awt.Component;

public class Animation implements Runnable, WebcamListener {
  final Component outputComponent;
  final JOCLSimpleImage joclSimpleImage;

  Animation(Component outputComponent, JOCLSimpleImage joclSimpleImage) {
    this.outputComponent = outputComponent;
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
    joclSimpleImage.inputImage.getGraphics().drawImage(we.getImage(), 0, 0, null);
    joclSimpleImage.sobelImage();
    outputComponent.repaint();
  }

  @Override
  public void run() {
    joclSimpleImage.webcam.addWebcamListener(this);
  }
}
