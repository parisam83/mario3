package com.parim.model.gameObjects.damages;

import com.parim.loader.ConfigLoader;

public class SpeedBomb extends Damage {
    private final double blockArea = ConfigLoader.getInstance().getProperty(Double.class, "SpeedBomb.blockArea");
    private final double multiplier = ConfigLoader.getInstance().getProperty(Double.class, "SpeedBomb.multiplier");
}
