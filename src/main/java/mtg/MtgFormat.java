package mtg;

public enum MtgFormat {
    STANDARD,
    MODERN;

    public String toString() {
        return this.name().toLowerCase();
    }
}
