package framework.core.layer;

public enum LayerType {
    ETHERNET("Ethernet"),
    INTERNET("Internet"),
    TRANSPORT("Transport"),
    APPLICATION("Application");

    private final String displayName;
    LayerType(String displayName) { this.displayName = displayName; }
    @Override public String toString() { return displayName; }
}
