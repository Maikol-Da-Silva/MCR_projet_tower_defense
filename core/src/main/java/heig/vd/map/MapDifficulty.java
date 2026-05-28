package heig.vd.map;

public enum MapDifficulty {
    EASY(new MapGenerationConfig(19, 13, 13, 2)),
    NORMAL(new MapGenerationConfig(15, 11, 9, 2)),
    HARD(new MapGenerationConfig(11, 9, 7, 2));

    private final MapGenerationConfig config;

    MapDifficulty(MapGenerationConfig config) {
        this.config = config;
    }

    public MapGenerationConfig config() {
        return config;
    }
}
