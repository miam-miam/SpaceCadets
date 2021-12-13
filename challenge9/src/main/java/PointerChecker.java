import java.awt.MouseInfo;
import java.awt.Point;

public class PointerChecker {
  private static final PointerChecker singleton = new PointerChecker();
  private Point lastPoint;
  private long lastMoveMillisecond;

  public PointerChecker() {
    lastMoveMillisecond = System.currentTimeMillis();
  }

  public static PointerChecker getInstance() {
    return singleton;
  }

  public int PointerChanged() {
    Point currentPoint = MouseInfo.getPointerInfo().getLocation();
    if (currentPoint.equals(lastPoint)) {
      return (int) (System.currentTimeMillis() - lastMoveMillisecond);
    } else {
      lastPoint = currentPoint;
      lastMoveMillisecond = System.currentTimeMillis();
      return 0;
    }
  }
}
