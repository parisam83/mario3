package com.parim.model.gameObjects.damages;

import com.parim.loader.ConfigLoader;

public class DamageBomb extends Damage {
    private final double blockArea = ConfigLoader.getInstance().getProperty(Double.class, "DamageBomb.blockArea");
    private final int percent = ConfigLoader.getInstance().getProperty(Integer.class, "DamageBomb.percent");

    public double getBlockArea() {
        return blockArea;
    }

    public int getPercent() {
        return percent;
    }
}
