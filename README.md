# mtgutil
Java Utility Classes related to Magic the Gathering (MTG)

The driver class currently downloads the most recent top metagame decks from Mtggoldfish (www.mtggoldfish.com) for a given MTG Format (Standard, Modern, etc.) into a standardized format.

For example this file format can used as input to the following application:
https://github.com/Cockatrice/Cockatrice

The driver will prompt you to enter the MTG Format:<br>
`Please enter a Mtg Format (e.g. Standard/Modern): `

Then, it will ask:<br>
`Would you like to output to a file path? (y/n): `
* If you answer `y`, it will prompt you for a full file path and it will create a directory named "/mtgutil-`MTGFORMAT`-`TIMESTAMP`" there. Then, it will put the deck files in that directory.
* Otherwise, it will print the deck lists to the console.

If you selected `y` above, it will ask:<br>
`Please enter the full file path: `<br>
Example Input: `C:\Users\Username\Documents\MtgDecks`
