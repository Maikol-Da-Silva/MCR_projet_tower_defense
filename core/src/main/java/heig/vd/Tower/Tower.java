package heig.vd.Tower;

import heig.vd.mob.*;
import heig.vd.utils.Position;
import java.util.List;

public class Tower {
    private Position position;
    private CombatTowerType type;
    private float cooldownTimer = 0f;

    public Tower(Position position, CombatTowerType type) {
        this.position = position;
        this.type = type;

    }

    public boolean isInRange(Mob mob) {
        if (mob.getPosition() == null) {
            return false;
        }
        double distance = position.distanceTo(mob.getPosition());
        return distance <= type.getRange();
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

    /**
     * Look for mobs in rage and attack if we can
     * @param delta
     * @param mobs
     */
    public void update(float delta, List<Mob> mobs) {
        // Decrement cooldown timer
        cooldownTimer -= delta;

        if (cooldownTimer <= 0) {
            Mob target = findTarget(mobs);
            if (target != null) {
                attack(target);

                // resets cooldown timer
                // each fire rate is two time previous than the one before
                // currently level 1 shoots one DMG per 3 seconds
                cooldownTimer = 3.0f /(float)(Math.pow(2,getFireRate()-1)); // Reset cooldown based on fire rate;
            }
        }
    }

    private Mob findTarget(List<Mob> mobs) {
        if (mobs == null || mobs.isEmpty()) {
            return null;
        }

        for (Mob mob : mobs) {
            if (isInRange(mob)) {
                return mob;
            }
        }
        return null;
    }
}
