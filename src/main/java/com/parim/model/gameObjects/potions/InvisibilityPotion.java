package com.parim.model.gameObjects.potions;

import com.parim.loader.ConfigLoader;

public class InvisibilityPotion extends Potion {
    private final int period = ConfigLoader.getInstance().getProperty(Integer.class, "InvisibilityPotion.period");

    public int getPeriod() {
        return period;
    }
}
