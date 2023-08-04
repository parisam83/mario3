package com.parim.model.gameObjects.damages;

import com.parim.loader.ConfigLoader;

public class Hammer extends Damage {
    private final int stunPeriod = ConfigLoader.getInstance().getProperty(Integer.class, "Hammer.stunPeriod");
}
