import requests
from bs4 import BeautifulSoup


def get_html():
    html_text = requests.get("https://typeracerdata.com/texts?sort=active_since&texts=full").text

    soup = BeautifulSoup(html_text, "html.parser")
    soup.find_all("tr")
    texts = soup.select("td > a[href^=\"/text?\"]", limit=1000)
    print("    <string-array name=\"texts\">")
    for text in texts:
        print("        <item>" + text.contents[0] + "</item>")
    print("    </string-array>")


if __name__ == "__main__":
    get_html()
