package com.parim.model.gameObjects;

import java.util.Date;

public abstract class Item {
    protected int COST, MAX_AMOUNT = -1, IN_TIME = -1, MAX_AMOUNT_BY_ONE_USER = -1, MIN_LEVEL = -1;
    protected boolean PAYS_WITH_DIAMOND;
    protected Date APPEAR_DATE, DISAPPEAR_DATE;

    protected int maxAmount = -1, maxAmountByOneUser = -1, timeToAppear = -1, timeToDisappear = -1, minLevel = -1;
    protected Date lastTimeBought;
    public int getCOST() {
        return COST;
    }

    public int getMAX_AMOUNT() {
        return MAX_AMOUNT;
    }

    public int getIN_TIME() {
        return IN_TIME;
    }

    public int getMAX_AMOUNT_BY_ONE_USER() {
        return MAX_AMOUNT_BY_ONE_USER;
    }

    public Date getAPPEAR_DATE() {
        return APPEAR_DATE;
    }

    public Date getDISAPPEAR_DATE() {
        return DISAPPEAR_DATE;
    }

    public int getMIN_LEVEL() {
        return MIN_LEVEL;
    }

    public boolean isPAYS_WITH_DIAMOND() {
        return PAYS_WITH_DIAMOND;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getMaxAmountByOneUser() {
        return maxAmountByOneUser;
    }

    public void setMaxAmountByOneUser(int maxAmountByOneUser) {
        this.maxAmountByOneUser = maxAmountByOneUser;
    }

    public int getTimeToAppear() {
        return timeToAppear;
    }

    public void setTimeToAppear(int timeToAppear) {
        this.timeToAppear = timeToAppear;
    }

    public int getTimeToDisappear() {
        return timeToDisappear;
    }

    public void setTimeToDisappear(int timeToDisappear) {
        this.timeToDisappear = timeToDisappear;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public Date getLastTimeBought() {
        return lastTimeBought;
    }

    @Override
    public String toString() {
        return "Item{" +
                "COST=" + COST +
                ", MAX_AMOUNT=" + MAX_AMOUNT +
                ", IN_TIME=" + IN_TIME +
                ", MAX_AMOUNT_BY_ONE_USER=" + MAX_AMOUNT_BY_ONE_USER +
                ", MIN_LEVEL=" + MIN_LEVEL +
                ", PAYS_WITH_DIAMOND=" + PAYS_WITH_DIAMOND +
                ", APPEAR_DATE=" + APPEAR_DATE +
                ", DISAPPEAR_DATE=" + DISAPPEAR_DATE +
                ", maxAmount=" + maxAmount +
                ", maxAmountByOneUser=" + maxAmountByOneUser +
                ", timeToAppear=" + timeToAppear +
                ", timeToDisappear=" + timeToDisappear +
                ", minLevel=" + minLevel +
                ", lastTimeBought=" + lastTimeBought +
                '}';
    }
}
