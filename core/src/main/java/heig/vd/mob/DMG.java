package heig.vd.mob;

import heig.vd.utils.DmgType;

public class DMG {
    private int amount;
    private DmgType type;

    public DMG(int amount, DmgType type) {
        this.amount = amount;
        this.type = type;
    }


    /* Getter Setter */
    public int getAmount() {return amount;}
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public DmgType getType() {return type;}
}
