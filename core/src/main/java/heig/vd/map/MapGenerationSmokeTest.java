package heig.vd.map;

import heig.vd.utils.Position;

public class MapGenerationSmokeTest {
    public static void main(String[] args) {
        MapGenerator generator = new MapGenerator();

        for (MapDifficulty difficulty : MapDifficulty.values()) {
            GameMap map = generator.generate(difficulty);
            System.out.println("=== " + difficulty + " ===");
            System.out.println("size=" + map.getWidth() + "x" + map.getHeight()
                    + ", roadTiles=" + map.getRoadTileCount()
                    + ", spots=" + map.countTiles(TileType.TOWER_SPOT));
            printAscii(map);
            System.out.println();
        }
    }

    private static void printAscii(GameMap map) {
        for (int y = map.getHeight() - 1; y >= 0; y--) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < map.getWidth(); x++) {
                line.append(symbolFor(map.getTile(new Position(x, y))));
            }
            System.out.println(line);
        }
    }

    private static char symbolFor(TileType type) {
        return switch (type) {
            case GRASS -> '.';
            case ROAD -> '#';
            case TOWER_SPOT -> 'T';
            case START -> 'S';
            case END -> 'E';
        };
    }
}
