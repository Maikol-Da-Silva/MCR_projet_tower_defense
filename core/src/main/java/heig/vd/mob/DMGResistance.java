package heig.vd.mob;

public class DMGResistance extends  DMGHandler {
    public DMGResistance(DMGHandler next) {
        super(next);
    }

    @Override
    public void handle(Mob mob, DMG damage) {
        if (mob.getResistance().contains(damage.getType())) {
            damage.setAmount(damage.getAmount() / 2);
        }

        super.handle(mob, damage);
    }
}
