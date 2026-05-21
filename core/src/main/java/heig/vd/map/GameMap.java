package heig.vd.map;

import heig.vd.utils.Position;
import java.util.*;


public class GameMap {
    private int width;
    private int height;
    private List<Position> path;          // path for mob
    private Position spawnPoint;          // Spawn location for mob
    private List<Position> towerSlots;     // Allowed positions for towers

    public GameMap(int width, int height, Position spawnPoint, List<Position> towerSlots, List<Position> path) {
        this.width = width;
        this.height = height;
        this.spawnPoint = spawnPoint;
        this.path = path ;
        this.towerSlots = towerSlots;
    }



    public boolean isInside(Position pos) {
        if (pos == null) return false;
        float col = pos.getCol();
        float row = pos.getRow();
        return col >= 0 && row >= 0 && col < width && row < height;
    }

    public List<Position> getPath() { return path; }

    public Position getSpawnPoint() { return spawnPoint; }

    public boolean isTowerSlot(Position pos) {
        return pos != null && towerSlots.contains(pos);
    }

    public List<Position> getTowerSlots() { return towerSlots; }
}
