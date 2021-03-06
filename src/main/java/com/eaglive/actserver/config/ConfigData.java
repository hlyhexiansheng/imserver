package com.eaglive.actserver.config;

import com.eaglive.actserver.db.DBConnectionInfo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by admin on 2015/11/6.
 */
public class ConfigData {
    public static String redisHost;
    public static int redisPort;
    public static int redisMaxIdle;
    public static int redisMaxWaitMills;
    public static int serverPort;
    public static String apiUrl;
    public static String badwordFileName;

    public static String DB_IP;
    public static String DB_DATABASE_NAME;
    public static String DB_USERNAME;
    public static String DB_PASSWORD;


    public static DBConnectionInfo eagLiveInfo = new DBConnectionInfo();
    public static DBConnectionInfo cloudLiveInfo = new DBConnectionInfo();

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





