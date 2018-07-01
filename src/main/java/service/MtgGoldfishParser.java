package service;

import mtg.MtgFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MtgGoldfishParser {

    public static final String MTG_GOLDFISH_BASE_URL = "https://www.mtggoldfish.com";
    public static final String MTG_GOLDFISH_METAGAME_URL = "https://www.mtggoldfish.com/metagame/";
    public static final String SIDEBOARD_PREFIX = "SB: ";

    public static void parseTopDecks(MtgFormat mtgFormat, String filePath) throws IOException {
        String url = MTG_GOLDFISH_METAGAME_URL + mtgFormat;
        Document document = Jsoup.connect(url).get();

        String containsQuery = "*=archetype/" + mtgFormat;
        String cssQuery = String.format("a[href%s]", containsQuery);

        Elements links = document.select(cssQuery);

        String outputDirectory = null;
        if (filePath != null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            outputDirectory = String.format(filePath + "/mtgutil-%s-%s", mtgFormat, timestamp);
            new File(outputDirectory).mkdirs();
        }

        for (Element link : links) {
            if (!isDuplicateLink(link)) {//Only parse the deck links that aren't duplicates containing tags

                String linkUrl = link.attr("href");
                String deckName = parseDeckName(linkUrl);
                String deckUrl = MTG_GOLDFISH_BASE_URL + linkUrl;
                System.out.println("Retrieving deck: " + deckUrl);

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

        String deckBaseQuery = "table.deck-view-deck-table";

        Elements decks = document.select(deckBaseQuery);//This will return 3 copies of the same deck: paper, online, and arena
        Element deck = decks.get(0);//Just use the first copy of the deck
        Element tableBody = deck.children().get(0);//Just get the elements from the first tbody tag
        Elements tableRows = tableBody.children();

        boolean isSideboard = false;//Toggles to true when the sideboard card lines are reached
        for (Element tableRow : tableRows) {
            Elements tableCells = tableRow.children();

            Elements deckHeaders = tableCells.select("td.deck-header");
            for (Element deckHeader : deckHeaders) {
                if (deckHeader.text().contains("Sideboard")) {
                    isSideboard = true;
                }
            }

            Elements quantitys = tableCells.select("td.deck-col-qty");
            Elements cardNames = tableCells.select("td.deck-col-card > a");

            if (!quantitys.isEmpty() && !cardNames.isEmpty()) {
                Element quantity = quantitys.get(0);
                Element cardName = cardNames.get(0);
                String line = "";
                if (isSideboard) {
                    line = line + SIDEBOARD_PREFIX;
                }
                line = line + quantity.text() + " " + cardName.text();

                if (printWriter == null) {
                    System.out.println(line);
                } else {
                    printWriter.println(line);
                }
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

    private static String parseDeckName(String url) {
        String[] linkUrlSegments = url.split("/");
        return linkUrlSegments[linkUrlSegments.length-1];
    }
}
