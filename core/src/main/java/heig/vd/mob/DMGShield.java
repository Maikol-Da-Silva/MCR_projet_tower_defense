package heig.vd.mob;

public class DMGShield extends DMGHandler{
    public DMGShield(DMGHandler next) {
        super(next);
    }

    @Override
    public void handle(Mob mob, DMG damage) {
        if (!mob.isShield()) {
            super.handle(mob, damage);
        }else {
            mob.setShield(false); //absorb damage
        }
    }
}
