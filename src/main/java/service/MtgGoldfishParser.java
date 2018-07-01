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

    public static final String TOP_DECKS_QUERY = "a[href*=archetype/%s]";
    public static final String DECK_QUERY = "table.deck-view-deck-table";
    public static final String DECK_HEADER_QUERY = "td.deck-header";
    public static final String CARD_QUANTITY_QUERY = "td.deck-col-qty";
    public static final String CARD_NAME_QUERY = "td.deck-col-card > a";

    public static final String SIDEBOARD_HEADER = "Sideboard";
    public static final String SIDEBOARD_PREFIX = "SB: ";

    /**
     *
     * Downloads the top metagame decks for the specified MTG format and puts them in the specified filePath, if provided.
     * Otherwise, prints the decks to the console.
     *
     * @param mtgFormat The MTG playing format that this run is for. E.g. STANDARD, MODERN, etc.
     * @param filePath The desired output file path for the MTG decks (Optional)
     * @throws IOException if the URL is not available, or if there was an error writing to the file path
     */
    public static void parseTopDecks(MtgFormat mtgFormat, String filePath) throws IOException {
        String url = MTG_GOLDFISH_METAGAME_URL + mtgFormat;
        Document document = Jsoup.connect(url).get();
        Elements links = document.select(String.format(TOP_DECKS_QUERY, mtgFormat));
        String outputDirectory = createFileDirectory(filePath, mtgFormat);

        for (Element link : links) {
            if (!isDuplicateLink(link)) {//Only parse the deck links that aren't duplicates containing tags
                String linkUrl = link.attr("href");
                String deckName = getLastUrlSegment(linkUrl);
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

    /**
     *
     * Downloads the deck and uses the PrintWriter to write to the output file, if that is provided.
     * Otherwise, print the deck to the console.
     *
     * @param url The URL of the deck
     * @param printWriter the Writer that handles writing the output to the file path
     * @throws IOException if the URL is not available, or if there was an error writing to the file path
     */
    public static void parseDeck(String url, PrintWriter printWriter) throws IOException {
        Document document = Jsoup.connect(url).get();

        Elements decks = document.select(DECK_QUERY);//This will return 3 copies of the same deck: paper, online, and arena
        Element deck = decks.get(0);//Just use the first copy of the deck
        Element tableBody = deck.children().get(0);//Just get the elements from the first tbody tag
        Elements tableRows = tableBody.children();

        boolean isSideboard = false;//Toggles to true when the sideboard card lines are reached
        for (Element tableRow : tableRows) {
            Elements tableCells = tableRow.children();

            Elements deckHeaders = tableCells.select(DECK_HEADER_QUERY);
            for (Element deckHeader : deckHeaders) {
                if (deckHeader.text().contains(SIDEBOARD_HEADER)) {
                    isSideboard = true;
                }
            }

            Elements quantities = tableCells.select(CARD_QUANTITY_QUERY);
            Elements cardNames = tableCells.select(CARD_NAME_QUERY);
            if (!quantities.isEmpty() && !cardNames.isEmpty()) {
                Element quantity = quantities.get(0);
                Element cardName = cardNames.get(0);
                String line = generateCardLine(quantity.text(), cardName.text(), isSideboard);

                if (printWriter == null) {
                    System.out.println(line);
                } else {
                    printWriter.println(line);
                }
            }
        }
    }

    /**
     *
     * Downloads the deck and prints the deck to the console.
     *
     * @param url The URL of the deck
     * @throws IOException if the URL is not available
     */
    public static void parseDeck(String url) throws IOException {
        parseDeck(url, null);
    }

    private static String createFileDirectory(String filePath, MtgFormat mtgFormat) {
        if (filePath == null) {
            return null;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String outputDirectory = String.format(filePath + "/mtgutil-%s-%s", mtgFormat, timestamp);
        new File(outputDirectory).mkdirs();
        return outputDirectory;
    }

    private static String generateCardLine(String quantity, String cardName, boolean isSideboard) {
        String line = "";
        if (isSideboard) {
            line = line + SIDEBOARD_PREFIX;
        }
        line = line + quantity + " " + cardName;

        return line;
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

    private static String getLastUrlSegment(String url) {
        if (url == null || !url.contains("/")) {
            return url;
        }

        String[] linkUrlSegments = url.split("/");
        return linkUrlSegments[linkUrlSegments.length-1];
    }
}
