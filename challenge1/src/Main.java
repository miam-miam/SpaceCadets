import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

  public static void main(String[] args) {
    // write your code here
    String cookie_name = "";
    String cookie_value = "";
    Document doc;
    try {
      doc =
          Jsoup.connect("https://secure.ecs.soton.ac.uk/people/?nameq=a")
              .cookie(cookie_name, cookie_value)
              .get();
    } catch (java.io.IOException e) {
      System.out.println("Could not connect to the web site... Are you connected to the internet?");
      return;
    }
    if (doc.title().equals("Redirecting")) {
      System.out.println(
          "Could not access the secure website... Are you sure the cookie details are correct?");
      return;
    }
    Elements table = doc.select("table[summary] tr ~ tr");
    int count = 0;
    for (Element col : table) {
      count += 1;
      String Name = col.select("td:eq(1) a[href^=\"https://\"]").text();
      String Role = col.select("td:eq(2)").text();
      String Email = col.select("td:eq(4) a").attr("href").substring(7);
      String Phone = col.select("td:eq(5)").text();
      if (!Phone.equals("")) {
        Phone = ", Phone number: " + Phone;
      }
      System.out.printf("Name: %s, Role: %s, Email: %s%s\n", Name, Role, Email, Phone);
      if (count == 10) {
        System.out.printf("Showed first %s results", count);
        break;
      }
    }
  }
}
