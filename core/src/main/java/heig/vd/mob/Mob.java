package heig.vd.mob;

import heig.vd.utils.DmgType;
import heig.vd.utils.Position;
import heig.vd.utils.TypeMob;

import java.util.ArrayList;
import java.util.List;

public class Mob {
    private int health;
    private int currentHealth;
    private List<DmgType> resistances;
    private boolean shield;
    private Position position;
    private float moveCooldown = 0f;
    private float moveInterval;
    private final TypeMob type;


    public  Mob(Position position, float speed, int health, TypeMob type, DmgType ...resistance) {
        this.position = position;
        this.moveInterval = speed;
        this.health = health;
        this.currentHealth = health;
        this.resistances = List.of(resistance);
        this.type = type;
    }

    public void takeDamage(DMGHandler dmgHandler, DMG dmg) {
        dmgHandler.handle(this, dmg);
    }

    /* Getter / Setter */
    public float getSpeed() {return moveInterval;}
    public void setSpeed(float speed) {this.moveInterval = speed;}

    public int getHealth() {return health;}

    public int getCurrentHealth() {return currentHealth;}
    public void setCurrentHealth(int currentHealth) {this.currentHealth = currentHealth;}

    public List<DmgType> getResistance() {return resistances;}

    public boolean isShield() {return shield;}
    public void setShield(boolean shield) {this.shield = shield;}

    public Position getPosition() {return position;}
    public void setPosition(Position position) {this.position = position;}

    public TypeMob getType() {return type;}

    public boolean canMove(float deltaTime) {
        moveCooldown += deltaTime;

        if (moveCooldown >= moveInterval) {
            moveCooldown -= moveInterval;
            return true;
        }

        return false;
    }
}
