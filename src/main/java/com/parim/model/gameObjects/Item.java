package com.parim.model.gameObjects;

public abstract class Item {
    protected int Cost, MaxAmount = -1, InTime = -1, MaxAmountByOneUser = -1, TimeToAppear = -1, TimeToDisappear = -1, MinLevel = -1;
    protected boolean[] HowToPay = new boolean[2]; // 0: coin, 1: diamond

    protected int maxAmount = -1, inTime = -1, maxAmountByOneUser = -1, timeToAppear = -1, timeToDisappear = -1, minLevel = -1;
    protected boolean[] howToPay = new boolean[2];
}
