package com.eaglive.actserver.config;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by admin on 2015/11/6.
 */
public class ConfigData {
    public static String redisHost;
    public static int redisPort;
    public static int serverPort;
    public static String apiUrl;

    public static final String LOST_CHANNEL_INVALID_CMD = "Lost Channel, Invalid Cmd!";
    public static final String LOST_CHANNEL_NOT_LOGIN = "Lost Channel, Not Login";
    public static final String LOST_CHANNEL_INVALID_TOKEN = "Lost Channel, Invalid Token";

    private static AtomicLong nextMsgId = new AtomicLong();
    public static long nextMsgId() {
        return nextMsgId.incrementAndGet();
    }
    public static void setLastMsgId(long msgId) {
        nextMsgId.set(msgId);
    }
    @Override
    public String toString() {
        return "ConfigData{" +
                "redisHost='" + redisHost + '\'' +
                ", redisPort=" + redisPort +
                '}';
    }
}





