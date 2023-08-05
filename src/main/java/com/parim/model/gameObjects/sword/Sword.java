package com.parim.model.gameObjects.sword;

import com.parim.loader.ConfigLoader;
import com.parim.model.gameObjects.Item;

public class Sword extends Item {
    private final double blockRange = ConfigLoader.getInstance().getProperty(Double.class, "Sword.blockRange");
    private final int percentDamage = ConfigLoader.getInstance().getProperty(Integer.class, "Sword.percentDamage");
    private final double pushBackBlock = ConfigLoader.getInstance().getProperty(Double.class, "Sword.pushBackBlock");
    private final int coolDown = ConfigLoader.getInstance().getProperty(Integer.class, "Sword.coolDown");

    public Sword(){
        IN_TIME = 24*3600;
        MAX_AMOUNT_BY_ONE_USER = 1;
        PAYS_WITH_DIAMOND = true;
        COST = 121;
    }

    public double getBlockRange() {
        return blockRange;
    }

    public int getPercentDamage() {
        return percentDamage;
    }

    public double getPushBackBlock() {
        return pushBackBlock;
    }

    public int getCoolDown() {
        return coolDown;
    }
}
