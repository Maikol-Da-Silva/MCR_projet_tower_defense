package heig.vd.tower;

import heig.vd.mob.*;
import heig.vd.utils.Position;

public class Tower {
    private Position position;
    private CombatTowerType type;

    public Tower(Position position, CombatTowerType type) {
        this.position = position;
        this.type = type;
    }

    public boolean isInRange(Mob mob) {
        return false;
        //TODO
    }

    public void attack(Mob mob) {
        DMGHandler chain = new DMGShield(new DMGStatus(new DMGResistance(new DMGHealth(null))));

        if (this.isInRange(mob)) {
            DMG damage = new DMG(type.getDamage(), type.getDamageType());
            mob.takeDamage(chain, damage);
        }
    }

    // Getters
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public CombatTowerType getType() { return type; }

    public DMG getDamage() {
        return new DMG(type.getDamage(), type.getDamageType());
    }

    public int getRange() { return type.getRange(); }

    public int getFireRate() { return type.getFireRate(); }
}
