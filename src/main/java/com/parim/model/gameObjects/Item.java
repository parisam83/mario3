package com.parim.model.gameObjects;

import java.util.Date;

public abstract class Item {
    protected int COST, AMOUNT_LEFT = -1, IN_TIME = -1, MAX_AMOUNT_BY_ONE_USER = -1, MIN_LEVEL = -1;
    protected boolean PAYS_WITH_DIAMOND;
    protected Date APPEAR_DATE, DISAPPEAR_DATE, LAST_TIME_BOUGHT;

    public int getCOST() {
        return COST;
    }

    public void setCOST(int COST) {
        this.COST = COST;
    }

    public void itemBought(){
        AMOUNT_LEFT--;
        LAST_TIME_BOUGHT = new Date();
    }
    public int getAMOUNT_LEFT() {
        return AMOUNT_LEFT;
    }

    public void setAMOUNT_LEFT(int AMOUNT_LEFT) {
        this.AMOUNT_LEFT = AMOUNT_LEFT;
    }

    public int getIN_TIME() {
        return IN_TIME;
    }

    public void setIN_TIME(int IN_TIME) {
        this.IN_TIME = IN_TIME;
    }

    public int getMAX_AMOUNT_BY_ONE_USER() {
        return MAX_AMOUNT_BY_ONE_USER;
    }

    public void setMAX_AMOUNT_BY_ONE_USER(int MAX_AMOUNT_BY_ONE_USER) {
        this.MAX_AMOUNT_BY_ONE_USER = MAX_AMOUNT_BY_ONE_USER;
    }

    public int getMIN_LEVEL() {
        return MIN_LEVEL;
    }

    public void setMIN_LEVEL(int MIN_LEVEL) {
        this.MIN_LEVEL = MIN_LEVEL;
    }

    public boolean isPAYS_WITH_DIAMOND() {
        return PAYS_WITH_DIAMOND;
    }

    public void setPAYS_WITH_DIAMOND(boolean PAYS_WITH_DIAMOND) {
        this.PAYS_WITH_DIAMOND = PAYS_WITH_DIAMOND;
    }

    public Date getAPPEAR_DATE() {
        return APPEAR_DATE;
    }

    public void setAPPEAR_DATE(Date APPEAR_DATE) {
        this.APPEAR_DATE = APPEAR_DATE;
    }

    public Date getDISAPPEAR_DATE() {
        return DISAPPEAR_DATE;
    }

    public void setDISAPPEAR_DATE(Date DISAPPEAR_DATE) {
        this.DISAPPEAR_DATE = DISAPPEAR_DATE;
    }

    public Date getLAST_TIME_BOUGHT() {
        return LAST_TIME_BOUGHT;
    }

    public void setLAST_TIME_BOUGHT(Date LAST_TIME_BOUGHT) {
        this.LAST_TIME_BOUGHT = LAST_TIME_BOUGHT;
    }
}