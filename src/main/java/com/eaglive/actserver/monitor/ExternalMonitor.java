package com.eaglive.actserver.monitor;

import com.eaglive.actserver.config.ConfigData;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;

/**
 * Created by admin on 2015/11/13.
 */
public class ExternalMonitor extends JedisPubSub implements Runnable{
    private final static String ADD_NOTIFICATION = "addNotification";
    private final static String ADD_PUSH = "addPush";
    private final static String PUSH_ALIAS_URL = ConfigData.apiUrl + "?cmd=pushaliasmsg";
    private final Jedis jedis;
    private final OkHttpClient okHttpClient;
    public ExternalMonitor(Jedis jedis) {
        this.jedis = jedis;
        this.okHttpClient = new OkHttpClient();
    }

    public void run() {
        jedis.subscribe(this, ADD_NOTIFICATION, ADD_PUSH);
    }

    @Override
    public void onMessage(String channel, String message) {
        System.out.println("ExternalMonitor, channel:"+channel+", message:"+message);
        if(channel.equals(ADD_NOTIFICATION)) {
            handleAddNotification(message);
        } else if(channel.equals(ADD_PUSH)) {
            handleAddPush(message);
        }
    }

    private void handleAddNotification(String message) {
        System.out.println("handleAddNotification:" + message);
    }
    
    private void handleAddPush(String message) {
        System.out.println("handleAddPush:" + message);
        System.out.println(PUSH_ALIAS_URL);
        final Request request = new Request.Builder().url(PUSH_ALIAS_URL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            public void onFailure(Request request, IOException e) {
            }
            public void onResponse(Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }
}
