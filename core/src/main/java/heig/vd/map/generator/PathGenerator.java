package heig.vd.map.generator;

import heig.vd.map.GameMap;
import heig.vd.map.TileType;
import heig.vd.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PathGenerator {

    public List<Position> generate(GameMap map, Random random) {
        map.reset();

        List<Position> roadTiles = new ArrayList<>();
        boolean horizontalRoute = random.nextBoolean();

        buildRoute(map, roadTiles, random, horizontalRoute);
        map.setPath(roadTiles);

        return roadTiles;
    }

    private void buildRoute(GameMap map, List<Position> roadTiles, Random random, boolean horizontal) {
        if (horizontal) {
            int startY = pickInteriorCoordinate(map.getHeight(), random);
            int endY = pickDifferentInteriorCoordinate(map.getHeight(), startY, random);
            int turnX = pickMiddleCoordinate(map.getWidth(), random);

            drawHorizontal(map, roadTiles, 0, turnX, startY);
            drawVertical(map, roadTiles, turnX, startY, endY);
            drawHorizontal(map, roadTiles, turnX, map.getWidth() - 1, endY);

            map.setStart(new Position(0, startY));
            map.setEnd(new Position(map.getWidth() - 1, endY));
        } else {
            int startX = pickInteriorCoordinate(map.getWidth(), random);
            int endX = pickDifferentInteriorCoordinate(map.getWidth(), startX, random);
            int turnY = pickMiddleCoordinate(map.getHeight(), random);

            drawVertical(map, roadTiles, startX, 0, turnY);
            drawHorizontal(map, roadTiles, startX, endX, turnY);
            drawVertical(map, roadTiles, endX, turnY, map.getHeight() - 1);

            map.setStart(new Position(startX, 0));
            map.setEnd(new Position(endX, map.getHeight() - 1));
        }
    }

    private void drawHorizontal(GameMap map, List<Position> roadTiles, int fromX, int toX, int y) {
        int step = fromX <= toX ? 1 : -1;
        int x = fromX;
        do {
            addRoad(map, roadTiles, x, y);
            x += step;
        } while (x != toX + step);
    }

    private void drawVertical(GameMap map, List<Position> roadTiles, int x, int fromY, int toY) {
        int step = fromY <= toY ? 1 : -1;
        int y = fromY;
        do {
            addRoad(map, roadTiles, x, y);
            y += step;
        } while (y != toY + step);
    }

    private void addRoad(GameMap map, List<Position> roadTiles, int x, int y) {
        Position pos = new Position(x, y);
        if (map.isInside(pos) && map.getTile(pos) == TileType.GRASS) {
            map.setTile(pos, TileType.ROAD);
            roadTiles.add(pos);
        }
    }

    // Wrapper methods removed: calls in build* now use utility pickXXXCoordinate methods directly

    private int pickInteriorCoordinate(int size, Random random) {
        if (size <= 2) {
            return 0;
        }
        return 1 + random.nextInt(size - 2);
    }

    private int pickDifferentInteriorCoordinate(int size, int forbidden, Random random) {
        if (size <= 2) {
            return 0;
        }

        if (size <= 3) {
            return forbidden == 0 ? size - 1 : 0;
        }

        int value = pickInteriorCoordinate(size, random);
        if (value != forbidden) {
            return value;
        }

        return value == size - 2 ? value - 1 : value + 1;
    }

    private int pickMiddleCoordinate(int size, Random random) {
        if (size <= 2) {
            return 0;
        }

        int min = Math.max(1, size / 3);
        int max = Math.max(min, size - 2 - size / 3);
        if (min >= max) {
            return size / 2;
        }
        return min + random.nextInt(max - min + 1);
    }
}
