package com.eaglive.actserver.task;

import com.eaglive.actserver.ActServer;
import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by admin on 2015/11/27.
 */
public class ScanActivityTask extends TimerTask {

    private final OkHttpClient httpClient = new OkHttpClient();
    @Override
    public void run() {
        return;
//        try {
//            List<String> activities = getActivityList();
//            long timestamp = BaseUtil.timestamp();
//            for(String activity : activities) {
//                if(timestamp - getActivityLiveTime(activity) > 120) {
//                    closeActivity(activity);
//                }
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
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
        List<String> activities;
        String url = ConfigData.apiUrl + "?cmd=getliveactvitylist";
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = (JsonObject) parser.parse(response.body().string());
        activities = JsonUtil.to(jsonObject.get("data").toString());
        return  activities;
    }

    private void closeActivity(String activity) {

    }
}
