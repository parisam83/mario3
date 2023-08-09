package com.parim.event;

public class UserShopEvent implements FormEvent {
    private int coins, diamond;
    public UserShopEvent(){}
    public UserShopEvent(int coins, int diamond) {
        this.coins = coins;
        this.diamond = diamond;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }
}
