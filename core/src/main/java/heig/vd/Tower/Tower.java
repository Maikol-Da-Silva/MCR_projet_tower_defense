package heig.vd.tower;

import heig.vd.mob.*;
import heig.vd.utils.Position;

public class Tower {
    private Position position;
    private DMG damage;
    private int range;
    private int fire_rate;


    public Tower(Position position, DMG damage, int range, int fire_rate) {
        this.position = position;
        this.damage = damage;
        this.range = range;
        this.fire_rate = fire_rate;
    }

    public boolean isInRange(Mob mob) {
        return false;
        //TODO
    }

    public void attack(Mob mob) {
        DMGHandler chain = new DMGShield(new DMGStatus(new DMGResistance(new DMGHealth(null))));

        if (this.isInRange(mob)) {
            mob.takeDamage(chain, damage);
        }
    }


    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public DMG getDamage() { return damage; }

    public int getRange() { return range; }

    public int getFireRate() { return fire_rate; }

}
