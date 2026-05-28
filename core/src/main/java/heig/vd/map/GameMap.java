package heig.vd.map;

import heig.vd.utils.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameMap {
    private final int width;
    private final int height;
    private final TileType[][] tiles;
    private final long seed;
    private final MapDifficulty difficulty;

    private Position spawnPoint;
    private Position endPoint;
    private final List<Position> path;
    private final List<Position> towerSlots;

    public GameMap(int width, int height, long seed, MapDifficulty difficulty) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.difficulty = difficulty;
        this.tiles = new TileType[width][height];
        this.path = new ArrayList<>();
        this.towerSlots = new ArrayList<>();
        reset();
    }

    public GameMap(int width, int height, Position spawnPoint, List<Position> towerSlots, List<Position> path) {
        this(width, height, -1L, null);
        this.spawnPoint = spawnPoint;
        if (path != null) {
            this.path.addAll(path);
        }
        if (towerSlots != null) {
            this.towerSlots.addAll(towerSlots);
        }
    }

    public void reset() {
        for (int x = 0; x < width; x++) {
            Arrays.fill(tiles[x], TileType.GRASS);
        }
        spawnPoint = null;
        endPoint = null;
        path.clear();
        towerSlots.clear();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getSeed() {
        return seed;
    }

    public MapDifficulty getDifficulty() {
        return difficulty;
    }

    public TileType getTile(Position pos) {
        return pos == null ? null : tiles[(int) pos.getCol()][(int) pos.getRow()];
    }

    public void setTile(Position pos, TileType type) {
        if (pos != null) {
            tiles[(int) pos.getCol()][(int) pos.getRow()] = type;
        }
    }

    public boolean isInside(Position pos) {
        return pos != null && pos.getCol() >= 0 && pos.getCol() < width && pos.getRow() >= 0 && pos.getRow() < height;
    }

    public void setStart(Position pos) {
        spawnPoint = pos;
        setTile(pos, TileType.START);
    }

    public void setEnd(Position pos) {
        endPoint = pos;
        setTile(pos, TileType.END);
    }

    public Position getSpawnPoint() {
        return spawnPoint;
    }

    public Position getEndPoint() {
        return endPoint;
    }

    public List<Position> getPath() {
        return path;
    }

    public void setPath(List<Position> newPath) {
        path.clear();
        if (newPath != null) {
            path.addAll(newPath);
        }
    }

    public List<Position> getTowerSlots() {
        return towerSlots;
    }

    public void setTowerSlots(List<Position> newTowerSlots) {
        towerSlots.clear();
        if (newTowerSlots != null) {
            towerSlots.addAll(newTowerSlots);
        }
    }

    public boolean isTowerSlot(Position pos) {
        return pos != null && towerSlots.contains(pos);
    }

    public int countTiles(TileType type) {
        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] == type) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getRoadTileCount() {
        return countTiles(TileType.ROAD) + countTiles(TileType.START) + countTiles(TileType.END);
    }
}
