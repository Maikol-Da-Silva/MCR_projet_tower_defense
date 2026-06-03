package heig.vd.Tower;

import heig.vd.utils.DmgType;

/**
 * Enum of all combat tower types available for placement.
 * Each type has a unique sprite asset and characteristics.
 */
public enum CombatTowerType {
    ARCHER("Towers/Combat Towers/spr_tower_archer.png", 50, 8, 2, DmgType.ARROW),
    CANNON("Towers/Combat Towers/spr_tower_cannon.png", 100, 5, 3, DmgType.EXPLOSION),
    CROSSBOW("Towers/Combat Towers/spr_tower_crossbow.png", 75, 7, 2, DmgType.ARROW),
    ICE_WIZARD("Towers/Combat Towers/spr_tower_ice_wizard.png", 60, 9, 1, DmgType.GLACE),
    LIGHTNING("Towers/Combat Towers/spr_tower_lightning_tower.png", 90, 6, 2, DmgType.LIGHTNING),
    POISON_WIZARD("Towers/Combat Towers/spr_tower_poison_wizard.png", 70, 8, 1, DmgType.POISON);

    private final String assetPath;
    private final int damage;
    private final int range;
    private final int fireRate;
    private final DmgType damageType;

    CombatTowerType(String assetPath, int damage, int range, int fireRate, DmgType damageType) {
        this.assetPath = assetPath;
        this.damage = damage;
        this.range = range;
        this.fireRate = fireRate;
        this.damageType = damageType;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public int getDamage() {
        return damage;
    }

    public int getRange() {
        return range;
    }

    public int getFireRate() {
        return fireRate;
    }

    public DmgType getDamageType() {
        return damageType;
    }

    /**
     * Get display name for UI (removes underscores).
     */
    public String getDisplayName() {
        return name().replace("_", " ");
    }
}


