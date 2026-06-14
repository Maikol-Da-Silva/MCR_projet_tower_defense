package heig.vd.mob;

public class DMGStatus extends DMGHandler{
    public DMGStatus(DMGHandler next) {
        super(next);
    }

    @Override
    public void handle(Mob mob, DMG damage) {
        switch (damage.getType()) {
            case ARROW, GLACE, LIGHTNING:
                mob.setSpeed(mob.getSpeed() * 2);
                break;
            case EXPLOSION:
                this.next = new DMGHealth(next);
                break;
            case POISON:
                if (!mob.getResistance().isEmpty()) {
                    mob.getResistance().removeFirst();
                }
                break;
        }

        super.handle(mob, damage);
    }
}
