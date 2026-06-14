package heig.vd.mob;

import heig.vd.utils.DmgType;

import java.util.ArrayList;
import java.util.List;

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
                    List<DmgType> resistances = new ArrayList<>(mob.getResistance());
                    resistances.removeFirst();
                    mob.setResistances(resistances);
                }
                break;
        }

        super.handle(mob, damage);
    }
}
