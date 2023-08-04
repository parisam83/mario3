package com.parim.model.gameObjects.potions;

import com.parim.model.gameObjects.Item;

public abstract class Potion extends Item {
    public Potion(){
        MaxAmount = 10;
        InTime = 24*3600;
    }
}
