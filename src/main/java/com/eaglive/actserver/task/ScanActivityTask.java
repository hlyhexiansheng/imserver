package com.eaglive.actserver.task;

import com.eaglive.actserver.ActServer;
import com.eaglive.actserver.lib.HttpClient;
import com.eaglive.actserver.util.BaseUtil;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by admin on 2015/11/27.
 */
public class ScanActivityTask extends TimerTask {

    @Override
    public void run() {
        try {
            List<String> activities = getActivityList();
            long timestamp = BaseUtil.timestamp();
            for(String activity : activities) {
                long lastTime = getActivityLiveTime(activity);
                if(timestamp - lastTime > 120) {
                    System.out.println("close");
                    closeActivity(activity);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getActivityLiveTime(String activity) {
        Jedis jedis = ActServer.server.getJedis();
        try {
            String key = "activitylasttime_hash";
            if(jedis.hexists(key, activity)) {
                return Long.valueOf(jedis.hget(key, activity));
            } else {
                return 0;
            }
        } finally {
            jedis.close();
        }
    }

    private List<String> getActivityList() throws Exception{
        List<String> activities = new ArrayList<String>();
        HttpClient httpClient = new HttpClient();
        httpClient.setCmd("getliveactvitylist");
        httpClient.GET();
        if(httpClient.isSuccess()) {
            activities = httpClient.getDataArray();
        }
        return  activities;
    }

    private void closeActivity(String activity) {
        HttpClient httpClient = new HttpClient();
        httpClient.setCmd("activitystop");
        httpClient.addQueryParam("hash", activity);
        httpClient.GET();
    }
}
