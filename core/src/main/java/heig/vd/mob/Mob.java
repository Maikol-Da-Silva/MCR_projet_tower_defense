package heig.vd.mob;

import heig.vd.utils.DmgType;

public class Mob {
    private int speed;
    private int health;
    private int currentHealth;

    public  Mob(int speed, int health) {
        this.speed = speed;
        this.health = health;
        this.currentHealth = health;
    }

    public void takeDamage(DMGHandler dmgHandler, DMG dmg) {
        //TODO Créer la chaine avant
        dmgHandler.handle(this, dmg);
    }

    /* Getter / Setter */
    public int getSpeed() {return speed;}
    public void setSpeed(int speed) {this.speed = speed;}
    public int getHealth() {return health;}
    public void setHealth(int health) {this.health = health;}
    public int getCurrentHealth() {return currentHealth;}
    public void setCurrentHealth(int currentHealth) {this.currentHealth = currentHealth;}
}
