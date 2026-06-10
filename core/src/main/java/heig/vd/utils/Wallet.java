package heig.vd.utils;

public class Wallet {
    int coins;

    public Wallet(int coins) {
        this.coins = coins;
    }

    public int getCoins() { return coins; }

    public void add(int coins){
        this.coins += coins;
    }

    public boolean spend(int coins){
        if ( this.coins >= coins){
            this.coins-=coins;
            return true;
        }

        return false;
    }
}
