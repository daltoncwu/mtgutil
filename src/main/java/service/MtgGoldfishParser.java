package service;

import mtg.MtgFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MtgGoldfishParser {

    public static final String MTG_GOLDFISH_BASE_URL = "https://www.mtggoldfish.com";
    public static final String MTG_GOLDFISH_METAGAME_URL = "https://www.mtggoldfish.com/metagame/";

    public static void parseTopDecks(MtgFormat mtgFormat) throws IOException {
        String url = MTG_GOLDFISH_METAGAME_URL + mtgFormat;
        Document document = Jsoup.connect(url).get();

        String containsQuery = "*=archetype/" + mtgFormat;
        String cssQuery = String.format("a[href%s]", containsQuery);

        Elements links = document.select(cssQuery);
        for (Element link : links) {
            if (!isDuplicateLink(link)) {//Only parse the deck links that aren't duplicates containing tags
                //TODO: Don't print out the link URL and instead Jsoup to connect to that URL to pull the deck info
                System.out.println("\nlink : " + MTG_GOLDFISH_BASE_URL + link.attr("href"));
            }
        }
    }

    //TODO: Implement this and test parsing a single deck first from the Driver before looping through all decks
    public static void parseDeck(String url) {

    }

    /*
     * Return whether or not the link is a duplicate link that contain extra tags, such as the #online or #paper tags
     */
    private static boolean isDuplicateLink(Element link) {
        if (link.attr("href").contains("#")) {
            return true;
        } else {
            return false;
        }
    }
}
