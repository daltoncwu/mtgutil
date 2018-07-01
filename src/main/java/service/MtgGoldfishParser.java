package service;

import mtg.MtgFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class MtgGoldfishParser {

    public static final String MTG_GOLDFISH_BASE_URL = "https://www.mtggoldfish.com";
    public static final String MTG_GOLDFISH_METAGAME_URL = "https://www.mtggoldfish.com/metagame/";

    public static void parseTopDecks(MtgFormat mtgFormat, String filePath) throws IOException {
        String url = MTG_GOLDFISH_METAGAME_URL + mtgFormat;
        Document document = Jsoup.connect(url).get();

        String containsQuery = "*=archetype/" + mtgFormat;
        String cssQuery = String.format("a[href%s]", containsQuery);

        Elements links = document.select(cssQuery);

        String outputDirectory = null;
        if (filePath != null) {
            outputDirectory = filePath + "/mtgutil-" + mtgFormat;
            new File(outputDirectory).mkdirs();
        }

        for (Element link : links) {
            if (!isDuplicateLink(link)) {//Only parse the deck links that aren't duplicates containing tags
                String linkUrl = link.attr("href");
                String[] linkUrlSegments = linkUrl.split("/");
                String deckName = linkUrlSegments[linkUrlSegments.length-1];
                String deckUrl = MTG_GOLDFISH_BASE_URL + linkUrl;
                System.out.println(deckUrl);

                try {
                    TimeUnit.SECONDS.sleep(1);//politely throttle our requests
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (outputDirectory != null) {
                    PrintWriter printWriter = new PrintWriter(outputDirectory + "/" + deckName + ".txt", "UTF-8");
                    parseDeck(deckUrl, printWriter);
                    printWriter.close();
                } else {
                    parseDeck(deckUrl);
                }
            }
        }
    }

    public static void parseDeck(String url, PrintWriter printWriter) throws IOException {
        Document document = Jsoup.connect(url).get();

        String deckBaseQuery = "table.deck-view-deck-table tr";

        Elements cardQuantities = document.select(deckBaseQuery + " td.deck-col-qty");
        Elements cards = document.select(deckBaseQuery + " td.deck-col-card > a");

        int maxIndex;//Only increment i to the max index of the smaller of the two Lists
        if (cardQuantities.size() < cards.size()) {
            maxIndex = cardQuantities.size();
        } else {
            maxIndex = cards.size();
        }

        for (int i = 0; i < maxIndex; i++) {
            if (printWriter == null) {
                System.out.println(cardQuantities.get(i).text() + " " + cards.get(i).text());
            } else {
                printWriter.println(cardQuantities.get(i).text() + " " + cards.get(i).text());
            }
        }
    }

    public static void parseDeck(String url) throws IOException {
        parseDeck(url, null);
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
