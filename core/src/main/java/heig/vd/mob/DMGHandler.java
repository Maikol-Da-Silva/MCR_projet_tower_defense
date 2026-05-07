package heig.vd.mob;

public abstract class DMGHandler {
    protected DMGHandler next;

    public DMGHandler(DMGHandler next) {
        this.next = next;
    }

    public void handle(Mob mob, DMG damage) {
        if (this.next != null) {
            this.next.handle(mob, damage);
        }
    }
}
