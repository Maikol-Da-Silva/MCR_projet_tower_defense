package heig.vd.mob;

public class DMGHealth extends DMGHandler {

    public DMGHealth(DMGHandler next) {
        super(next);
    }

    @Override
    public void handle(Mob mob, DMG damage) {
        mob.setCurrentHealth(mob.getCurrentHealth() - damage.getAmount());
        super.handle(mob, damage);
    }
}
