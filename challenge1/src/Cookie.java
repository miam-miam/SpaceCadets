import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Cookie {
  private static final String DIR = "data";
  private static final String PATH = DIR + "\\cookie";
  String name = "";
  String value = "";

  public Cookie() {
    try {
      File myFile = new File(PATH);
      Scanner myReader = new Scanner(myFile);
      name = myReader.nextLine();
      value = myReader.nextLine();
      myReader.close();
    } catch (FileNotFoundException | NoSuchElementException e) {
      Update();
    }
  }

  /**
   * Updates the cookie used by asking the user, it then stores the cookie in a file so that it can
   * be used for later.
   */
  public void Update() {
    // Get Values from User using Scanner
    System.out.print(
        "To update the authentication cookie please login into https://secure.ecs.soton.ac.uk/ and copy the cookie name and value here.\nName (Should start with _shibsession_): ");
    Scanner in = new Scanner(System.in);
    name = in.nextLine();
    System.out.print("Cookie value: ");
    value = in.nextLine();

    try {
      // Update values in file so that it can be used in future runs.
      FileWriter writer = new FileWriter(PATH);
      writer.write(name + "\n" + value);
      writer.close();
    } catch (IOException e) {
      try {
        // May have failed as dir does not exist.
        Path path = Paths.get(DIR);
        Files.createDirectory(path);
        FileWriter writer = new FileWriter(PATH);
        writer.write(name + "\n" + value);
        writer.close();
      } catch (IOException ioException) {
        System.out.println("Could not create file to store cookies.");
      }
    }
  }
}
