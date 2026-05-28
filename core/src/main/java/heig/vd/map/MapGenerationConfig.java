package heig.vd.map;

public class MapGenerationConfig {
    public final int width;
    public final int height;
    public final int maxTowerSpots;
    public final int minTowerSpotSpacing;

    public MapGenerationConfig(int width, int height, int maxTowerSpots, int minTowerSpotSpacing) {
        this.width = width;
        this.height = height;
        this.maxTowerSpots = maxTowerSpots;
        this.minTowerSpotSpacing = minTowerSpotSpacing;
    }
}
