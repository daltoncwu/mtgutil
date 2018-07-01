# mtgutil
Java Utility Classes related to Magic the Gathering (MTG)

Currently, the Driver.java class runs the MtgGoldfishParser utility, which downloads the most recent top metagame decks from Mtggoldfish (www.mtggoldfish.com) for a given MTG Format (Standard, Modern, etc.) into a standardized format.

For example this file format can used as input to the following application:
https://github.com/Cockatrice/Cockatrice

You can run the Driver.java class as is, to output the decks to the console. Or you can add your desired file path to Driver.java, and this project will create a directory named "/mtgutil-MTGFORMAT-TIMESTAMP"
-MTGFORMAT is currently toggled within Driver.java
-TIMESTAMP is the current date and time

This project will then pull the top metagame decks from www.mtggoldfish.com and place them in that directory, if you specified a file path in Driver.java
