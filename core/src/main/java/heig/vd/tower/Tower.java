package heig.vd.tower;

import heig.vd.mob.*;
import heig.vd.utils.Position;
import java.util.ArrayList;
import java.util.List;

public class Tower {
    private static final float RELOAD_TIME = 20f;

    private Position position;
    private CombatTowerType type;
    private float cooldownTimer = 0f;

    // Damage chain-of-responsibility, injected by the TowerManager that owns this tower.
    private DMGHandler dmgChain;

    private final List<Projectile> projectiles = new ArrayList<>();

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
        if (this.isInRange(mob)) {
            DMG damage = new DMG(type.getDamage(), type.getDamageType());
            mob.takeDamage(dmgChain, damage);
        }
    }

    // Getters / Setters
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public CombatTowerType getType() { return type; }

    public void setDmgChain(DMGHandler dmgChain) { this.dmgChain = dmgChain; }

    public int getFireRate() { return type.getFireRate(); }

    /**
     * Look for mobs in range and attack if we can, and advance this tower's
     * in-flight projectiles.
     */
    public void update(float delta, List<Mob> mobs) {
        // Move existing projectiles; drop the ones that reached their target.
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(delta);
            if (projectile.hasArrived()) {
                projectiles.remove(i);
            }
        }

        // Decrement cooldown timer
        cooldownTimer -= delta;

        if (cooldownTimer <= 0) {
            Mob target = findTarget(mobs);
            if (target != null) {
                attack(target);

                // resets cooldown timer
                // each fire rate is two time previous than the one before
                cooldownTimer = RELOAD_TIME / getFireRate();

                // spawn the visual projectile, using the tower's fire rate as its travel speed
                projectiles.add(new Projectile(position, target.getPosition(), type, getFireRate()));
            }
        }
    }

    /**
     * The projectiles this tower currently has in flight (for rendering).
     */
    public List<Projectile> getProjectiles() {
        return projectiles;
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
