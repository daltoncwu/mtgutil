package mtg;

public enum MtgFormat {
    STANDARD,
    MODERN,
    PAUPER,
    LEGACY,
    VINTAGE,
    PENNY_DREADFUL,
    COMMANDER_1V1,
    COMMANDER,
    BRAWL,
    ARENA_STANDARD;


    public static MtgFormat getEnum(String string) {
        return valueOf(string.replace(" ", "_").toUpperCase());
    }

    public String toString() {
        return this.name().replace("_", " ").toLowerCase();
    }
}
