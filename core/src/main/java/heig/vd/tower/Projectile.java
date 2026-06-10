package heig.vd.tower;

import heig.vd.utils.Position;

/**
 * Visual projectile fired by a tower.
 */
public class Projectile {
    private final Position position;
    private final Position target;
    private final CombatTowerType type;
    private final float speed; // tiles per "tick" of the simulation
    private boolean arrived = false;

    public Projectile(Position start, Position target, CombatTowerType type, float speed) {
        // Copy both points so we never mutate the tower's or mob's own Position.
        this.position = new Position(start.getCol(), start.getRow());
        this.target = new Position(target.getCol(), target.getRow());
        this.type = type;
        this.speed = speed;
    }

    /**
     * Moves the projectile toward its target
     */
    public void update(float deltaTime) {
        float dx = target.getCol() - position.getCol();
        float dy = target.getRow() - position.getRow();
        float distance = (float) position.distanceTo(target);
        float step = speed * deltaTime;

        if (distance <= step || distance == 0f) {
            position.setCol(target.getCol());
            position.setRow(target.getRow());
            arrived = true;
            return;
        }

        position.setCol(position.getCol() + (dx / distance) * step);
        position.setRow(position.getRow() + (dy / distance) * step);
    }

    public boolean hasArrived() { return arrived; }

    public Position getPosition() { return position; }
    public CombatTowerType getType() { return type; }
}
