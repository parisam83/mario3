package com.parim.model.gameObjects.potions;

import com.parim.model.gameObjects.Item;

public abstract class Potion extends Item {
    public Potion(){
        MAX_AMOUNT = 10;
        IN_TIME = 24*3600;
        COST = 54;
    }
}
