package com.lxrtalk.texas.texas;

import com.google.gson.annotations.Expose;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 房间根据级别，固定参数配置
 */
public class TexasRoomConfig {

    /**
     * 房间级别
     */
    @Expose
    private int level;
    /**
     * 房间类型，0德州，1三张牌
     */
    @Expose
    private int type;

    /**
     * 允许带入的最大筹码
     */
    @Expose
    private int maxChips;
    /**
     * 允许带入的最小筹码
     */
    @Expose
    private int minChips;
    /**
     * 大盲下注筹码
     */
    @Expose
    private int bigBet;
    /**
     * 小盲下注筹码
     */
    @Expose
    private int smallBet;
    /**
     * 最大玩家数
     */
    @Expose
    private int maxPlayers;
    /**
     * 最小玩家数
     */
    @Expose
    private int minPlayers;
    /**
     * 两局之间的间隔时间,秒
     */
    @Expose
    private int restBetweenGame = 5000;

    /**
     * 操作超时时间，单位毫秒（玩家在规定时间内没有完成操作，则系统自动帮其弃牌）
     */
    @Expose
    private int optTimeout = 10000;

    public int getRestBetweenGame() {
        return restBetweenGame;
    }

    public void setRestBetweenGame(int restBetweenGame) {
        this.restBetweenGame = restBetweenGame;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMaxChips() {
        return maxChips;
    }

    public void setMaxChips(int maxChips) {
        this.maxChips = maxChips;
    }

    public int getMinChips() {
        return minChips;
    }

    public void setMinChips(int minChips) {
        this.minChips = minChips;
    }

    public int getBigBet() {
        return bigBet;
    }

    public void setBigBet(int bigBet) {
        this.bigBet = bigBet;
    }

    public int getSmallBet() {
        return smallBet;
    }

    public void setSmallBet(int smallBet) {
        this.smallBet = smallBet;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }


    public int getOptTimeout() {
        return optTimeout;
    }

    public void setOptTimeout(int optTimeout) {
        this.optTimeout = optTimeout;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
