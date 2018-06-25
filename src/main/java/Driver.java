import mtg.MtgFormat;
import service.MtgGoldfishParser;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) {
        try {
            MtgGoldfishParser.parseTopDecks(MtgFormat.STANDARD);
        } catch (IOException e) {
            System.out.println("Failed to parse the top decks.");
        }
    }
}
