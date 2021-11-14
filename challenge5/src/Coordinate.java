class Coordinate implements Comparable<Coordinate> {
  private final int max;
  public Integer x;
  public Integer y;

  /**
   * The Coordinate class is used in the TreeMap, it needed to be a class so that it could be
   * compared.
   *
   * @param x the x position of the coord
   * @param y the y position of the coord
   * @param max the maximum x position in the space.
   */
  public Coordinate(int x, int y, int max) {
    this.x = x;
    this.y = y;
    this.max = max;
  }

  /** @return A perfect hash that uniquely identifies a coord (Assuming no overflow) */
  @Override
  public int hashCode() {
    return x + y * max;
  }

  /**
   * An equals method was needed for the treemap
   *
   * @param o an object to compare to
   * @return whether they both have the same contents
   */
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
    return x.equals(c.x) && y.equals(c.y);
  }

  /**
   * Whether a coordinate comes before or after another one. The hash is used as it is "perfect".
   *
   * @param o the coordinate to compare to
   * @return a negative integer, zero, or a positive integer as this coordinate is less than, equal
   *     to, or greater than the other coordinate.
   */
  @Override
  public int compareTo(Coordinate o) {
    return hashCode() - o.hashCode();
  }
}
