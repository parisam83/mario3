package com.parim.model.gameObjects.potions;

import com.parim.model.gameObjects.Item;

public abstract class Potion extends Item {
    public Potion(){
        AMOUNT_LEFT = 10;
        IN_TIME = 24*3600;
        COST = 54;
    }
}
