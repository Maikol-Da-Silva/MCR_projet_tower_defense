package heig.vd.mob;

import heig.vd.utils.DmgType;
import heig.vd.utils.Position;
import java.util.List;

public class Mob {
    private int speed;
    private int health;
    private int currentHealth;
    private List<DmgType> resistances;
    private boolean shield;
    private Position pos;

    public  Mob(Position pos, int speed, int health, DmgType ...resistance) {
        this.pos = pos;
        this.speed = speed;
        this.health = health;
        this.currentHealth = health;
        this.resistances = List.of(resistance);
    }

    public void takeDamage(DMGHandler dmgHandler, DMG dmg) {
        dmgHandler.handle(this, dmg);
    }

    /* Getter / Setter */
    public int getSpeed() {return speed;}
    public void setSpeed(int speed) {this.speed = speed;}

    public int getHealth() {return health;}
    public void setHealth(int health) {this.health = health;}

    public int getCurrentHealth() {return currentHealth;}
    public void setCurrentHealth(int currentHealth) {this.currentHealth = currentHealth;}

    public List<DmgType> getResistance() {return resistances;}
    public void setResistances(List<DmgType> resistance) {this.resistances = resistance;}

    public boolean isShield() {return shield;}
    public void setShield(boolean shield) {this.shield = shield;}

    public Position getPos() {return pos;}
    public void setPos(Position pos) {this.pos = pos;}
}
