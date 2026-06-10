package heig.vd.tower;

import heig.vd.utils.DmgType;

/**
 * Enum of all combat tower types available for placement.
 * Each type has a unique sprite asset and characteristics.
 */
public enum CombatTowerType {
    ARCHER("Towers/Combat Towers/spr_tower_archer.png", 50, 8, 2, DmgType.ARROW, 20 ),
    CANNON("Towers/Combat Towers/spr_tower_cannon.png", 100, 5, 3, DmgType.EXPLOSION, 40),
    CROSSBOW("Towers/Combat Towers/spr_tower_crossbow.png", 75, 7, 2, DmgType.ARROW, 15 ),
    ICE_WIZARD("Towers/Combat Towers/spr_tower_ice_wizard.png", 60, 9, 1, DmgType.GLACE,15 ),
    LIGHTNING("Towers/Combat Towers/spr_tower_lightning_tower.png", 90, 6, 2, DmgType.LIGHTNING, 35 ),
    POISON_WIZARD("Towers/Combat Towers/spr_tower_poison_wizard.png", 70, 8, 1, DmgType.POISON, 30);

    private final String assetPath;
    private final int damage;
    private final int range;
    private final int fireRate;
    private final DmgType damageType;
    private final int price;


    CombatTowerType(String assetPath, int damage, int range, int fireRate, DmgType damageType, int price) {
        this.assetPath = assetPath;
        this.damage = damage;
        this.range = range;
        this.fireRate = fireRate;
        this.damageType = damageType;
        this.price = price;
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

    public int getPrice() { return price; }

    /**
     * Get display name for UI (removes underscores).
     */
    public String getDisplayName() {
        return name().replace("_", " ");
    }
}


