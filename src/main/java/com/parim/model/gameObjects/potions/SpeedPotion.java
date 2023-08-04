package com.parim.model.gameObjects.potions;

import com.parim.loader.ConfigLoader;

public class SpeedPotion extends Potion {
    private final int period = ConfigLoader.getInstance().getProperty(Integer.class, "SpeedPotion.period");
    private final double multiplier = ConfigLoader.getInstance().getProperty(Double.class, "SpeedPotion.multiplier");

    public int getPeriod() {
        return period;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
