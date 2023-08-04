package com.parim.model.gameObjects.potions;

import com.parim.loader.ConfigLoader;

public class HealthPotion extends Potion {
    private final int HPPercent = ConfigLoader.getInstance().getProperty(Integer.class, "HealthPotion.HPPercent");

    public int getHPPercent() {
        return HPPercent;
    }
}
