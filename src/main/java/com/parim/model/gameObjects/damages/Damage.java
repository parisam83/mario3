package com.parim.model.gameObjects.damages;

import com.parim.model.gameObjects.Item;

public abstract class Damage extends Item {
    public Damage(){
        MIN_LEVEL = 2;
        COST = 87;
    }
}