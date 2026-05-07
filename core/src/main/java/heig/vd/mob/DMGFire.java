package heig.vd.mob;

import heig.vd.utils.DmgType;

public class DMGFire extends DMGHandler {

    public DMGFire(DMGHandler next) {
        super(next);
    }

    @Override
    public void handle(Mob mob, DMG damage) {
        if (damage.getType() == DmgType.Feu) {
            mob.setCurrentHealth(mob.getCurrentHealth() - damage.getAmount());
        }

        super.handle(mob, damage);
    }
}
