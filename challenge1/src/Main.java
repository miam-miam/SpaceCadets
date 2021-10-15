import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

  public static void main(String[] args) {
    Cookie cookie = new Cookie();
    System.out.print("Please input your search query: ");
    Scanner in = new Scanner(System.in);
    // Removes all non letters and turns spaces into +
    String query = in.nextLine().replaceAll("[^a-zA-Z\\s]", "").replaceAll("[\\s]", "+");

    while (true) {
      Document doc;
      try {
        System.out.println("Searching the database...");
        // We use &picsize= so that the webpage doesn't send back photos and thus slow down our
        // program.
        doc =
            Jsoup.connect("https://secure.ecs.soton.ac.uk/people/?nameq=" + query + "&picsize=")
                .cookie(cookie.name, cookie.value)
                .get();
      } catch (java.io.IOException e) {
        System.out.println(
            "Could not connect to the web site... Are you connected to the internet?");
        return;
      }

      if (doc.title().equals("Redirecting")) {
        System.out.println(
            "Could not access the secure website... Are you sure the cookie details are correct?");
        cookie.Update();
        continue;
      }
      // Get the table containing all the people in ecs that match the search query.
      Elements table = doc.select("table[summary] tr ~ tr");
      if (table.isEmpty()) {
        System.out.println("Could not find the requested person...");
        return;
      }

      int count = 0;
      for (Element col : table) {
        count += 1;
        // Get the link (a) tag that contains a link to an https page who has as parent the first td
        // element of the table.
        String Name = col.select("td:eq(0) a[href^=\"https://\"]").text();
        String Role = col.select("td:eq(1)").text();
        // Get the mailto: link and then remove mailto: to produce the email address.
        String Email = col.select("td:eq(3) a").attr("href").substring(7);
        String Phone = col.select("td:eq(4)").text();
        if (!Phone.isEmpty()) {
          Phone = ", Phone number: " + Phone;
        }
        System.out.printf("Name: %s, Role: %s, Email: %s%s\n", Name, Role, Email, Phone);
        if (count == 10) {
          System.out.printf("Only showing the first %s results", count);
          return;
        }
      }
    }
  }
}
