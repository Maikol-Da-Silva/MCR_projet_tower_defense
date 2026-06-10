package heig.vd.tower;

import heig.vd.utils.Position;
import heig.vd.map.GameMap;
import java.util.*;

public class TowerManager {
    private final GameMap map;
    private final Map<Position, Tower> towers;

    public TowerManager(GameMap map) {
        this.map = map;
        this.towers = new HashMap<>();
    }

    public boolean placeTower(Position pos, Tower tower) {
        if (!map.isInside(pos) || !map.isTowerSlot(pos)) return false;
        if (isOccupied(pos)) return false;
        tower.setPosition(pos);
        towers.put(pos, tower);
        return true;
    }

    public boolean isOccupied(Position pos) {
        return towers.containsKey(pos);
    }


    public Tower removeTower(Position pos) {
        return towers.remove(pos);
    }


    public Tower getTower(Position pos) {
        return towers.get(pos);
    }


    public void clear() {
        towers.clear();
    }

}
