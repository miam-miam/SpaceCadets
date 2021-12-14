package Win32Calls;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import java.util.Arrays;
import java.util.List;

public interface User32 extends StdCallLibrary {
  User32 INSTANCE = Native.load("user32", User32.class);

  /**
   * @see <a
   *     href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getlastinputinfo">docs</a>
   * @return time of the last input event, in milliseconds
   */
  boolean GetLastInputInfo(LASTINPUTINFO result);

  /**
   * Contains the time of the last input.
   *
   * @see <a
   *     href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/ns-winuser-lastinputinfo">docs</a>
   */
  class LASTINPUTINFO extends Structure {
    public int cbSize = size();
    /// Tick count of when the last input event was received.
    public int dwTime;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("cbSize", "dwTime");
    }

    public static class ByReference extends LASTINPUTINFO implements Structure.ByReference {}
  }
}
