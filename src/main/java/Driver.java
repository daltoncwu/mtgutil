import mtg.MtgFormat;
import service.MtgGoldfishParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Driver {
    public static void main(String[] args) {
        String filePath = null;

        BufferedReader bufferedReader = null;
        MtgFormat mtgFormat = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Please enter a Mtg Format (e.g. Standard/Modern): ");
            String input = bufferedReader.readLine();
            mtgFormat = MtgFormat.valueOf(input.toUpperCase());

            System.out.println("Would you like to output to a file path? (y/n): ");
            input = bufferedReader.readLine();

            if (input.equalsIgnoreCase("y")) {
                System.out.println("Please enter the full file path: ");
                filePath = bufferedReader.readLine();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid value for MTG Format. Exiting.");
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (filePath == null || filePath.length() < 1) {
            System.out.println("Since file path was not provided, printing decks to console.");
        }

        try {
            MtgGoldfishParser.parseTopDecks(mtgFormat, filePath);
        } catch (IOException e) {
            System.out.println("Failed to parse the top decks.");
            e.printStackTrace();
        }
    }
}
