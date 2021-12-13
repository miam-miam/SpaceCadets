package Win32Calls;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {

  Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

  /**
   * Retrieves the number of milliseconds that have elapsed since the system was started.
   *
   * @return number of milliseconds that have elapsed since the system was started.
   * @see <a
   *     href="https://docs.microsoft.com/en-gb/windows/win32/api/sysinfoapi/nf-sysinfoapi-gettickcount">...</a>
   */
  int GetTickCount();
}
