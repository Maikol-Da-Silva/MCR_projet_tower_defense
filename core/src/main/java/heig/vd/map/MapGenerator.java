package heig.vd.map;

import heig.vd.map.generator.PathGenerator;
import heig.vd.map.generator.TowerSpotGenerator;

import java.util.Random;

public class MapGenerator {
    private final PathGenerator pathGenerator = new PathGenerator();
    private final TowerSpotGenerator towerSpotGenerator = new TowerSpotGenerator();

    public GameMap generate(MapDifficulty difficulty) {
        return generate(difficulty, System.nanoTime());
    }

    public GameMap generate(MapDifficulty difficulty, long seed) {
        MapGenerationConfig config = difficulty.config();
        Random random = new Random(seed);

        GameMap map = new GameMap(config.width, config.height, seed, difficulty);

        var solutionPath = pathGenerator.generate(map, random);
        towerSpotGenerator.generate(map, config, random, solutionPath);
        return map;
    }
}
