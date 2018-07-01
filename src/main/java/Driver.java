import mtg.MtgFormat;
import service.MtgGoldfishParser;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) {
        String filePath = null;
        //NOTE: Enter your desired output file path here
        //filePath = "Enter full file path here";

        if (filePath == null || filePath.length() < 1) {
            System.out.println("Note: to download directly to text files, you can enter your desired file path in Driver.java");
            System.out.println("If you do, a mtgutil directory will be created at the specified path and the deck files will be outputted there.");
            System.out.println("Since this was not provided, defaulting to printing decks to console.");
        }

        try {
            MtgFormat mtgFormat = MtgFormat.STANDARD;
            //MtgFormat mtgFormat = MtgFormat.MODERN;
            
            MtgGoldfishParser.parseTopDecks(mtgFormat, filePath);
        } catch (IOException e) {
            System.out.println("Failed to parse the top decks.");
            e.printStackTrace();
        }
    }
}
