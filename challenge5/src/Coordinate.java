import java.util.Objects;

public class Coordinate {
  public Integer x;
  public Integer y;
  private int max;
  public Coordinate(int x, int y, int max) {
    this.x = x;
    this.y = y;
    this.max = max;
  }

  @Override
  public int hashCode() {
    return x + y * max;
  }

  @Override
  public boolean equals(Object o) {

    if (o == this) {
      return true;
    }

    if (!(o instanceof Coordinate)) {
      return false;
    }

    // typecast o to Complex so that we can compare data members
    Coordinate c = (Coordinate) o;

    // Compare the data members and return accordingly
    return x.equals(c.x)
        && y.equals(c.y);
  }

}
