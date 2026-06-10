package heig.vd.mob;

import heig.vd.utils.Position;

import java.util.ArrayList;
import java.util.List;

public class MobManager {
    /* Constants */
    private final int BOSS_FACTOR = 2;
    private final int NB_BOSS = 2;
    private final float SPEED = 4f;

    /* Variables */
    private final int nbMob;
    private final int mobHealth;
    private List<Mob> mobs;
    private Position startpos;


    public MobManager(int nbMob, int mobHealth, Position pos) {
        this.nbMob = nbMob;
        this.mobs = new ArrayList<>();
        this.mobHealth = mobHealth;
        this.startpos = pos;
    }

    public void createWave(){
        mobs.clear();

        for (int i = 0; i < nbMob - NB_BOSS; i++){
            mobs.add(new Mob(startpos, SPEED, mobHealth));
        }

        for (int i = 0; i < NB_BOSS; i++){
            mobs.add(new Mob(startpos, SPEED / BOSS_FACTOR, mobHealth * BOSS_FACTOR));
        }
    }

    public List<Mob> getMobs(){ return mobs; }
}
