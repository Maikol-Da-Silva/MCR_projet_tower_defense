package heig.vd.mob;

import heig.vd.utils.DmgType;

import java.util.List;

public class DMGStatus extends DMGHandler{
    public DMGStatus(DMGHandler next) {
        super(next);
    }

    @Override
    public void handle(Mob mob, DMG damage) {
        //TODO trouver des idées
        switch (damage.getType()) {
            case ARROW:
                mob.setSpeed(mob.getSpeed() / 2);
                break;
            case GLACE:
                mob.setSpeed(mob.getSpeed() / 2);
                break;
            case LIGHTNING:
                mob.setSpeed(mob.getSpeed() / 2);
                break;
            case EXPLOSION:
                this.next = new DMGHealth(next);
                break;
            case POISON:
                mob.getResistance().removeFirst();
                break;
        }

        super.handle(mob, damage);
    }
}
