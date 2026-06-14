package heig.vd.tower;

import heig.vd.utils.Position;
import heig.vd.map.GameMap;
import heig.vd.mob.DMGHandler;
import heig.vd.mob.DMGShield;
import heig.vd.mob.DMGStatus;
import heig.vd.mob.DMGResistance;
import heig.vd.mob.DMGHealth;
import java.util.*;

public class TowerManager {
    private final GameMap map;
    private final Map<Position, Tower> towers;

    // Shared damage chain-of-responsibility handed to every tower this manager places.
    private final DMGHandler dmgChain;

    public TowerManager(GameMap map) {
        this.map = map;
        this.towers = new HashMap<>();
        this.dmgChain = new DMGShield(new DMGStatus(new DMGResistance(new DMGHealth(null))));
    }

    public boolean placeTower(Position pos, Tower tower) {
        if (!map.isInside(pos) || !map.isTowerSlot(pos)) return false;
        if (isOccupied(pos)) return false;
        tower.setPosition(pos);
        tower.setDmgChain(dmgChain);
        towers.put(pos, tower);
        return true;
    }

    public boolean isOccupied(Position pos) {
        return towers.containsKey(pos);
    }

    public Map<Position, Tower> getTowers() {
        return towers;
    }
}
