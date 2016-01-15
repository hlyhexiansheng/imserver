package com.eaglive.actserver.util;

import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.lib.HttpClient;
import com.google.gson.JsonObject;

/**
 * Created by admin on 2015/11/27.
 */
public class BaseUtil {
    public static long timestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static long lastMsgId() {
        HttpClient client = new HttpClient(ConfigData.apiUrl)
                .addQueryParam("cmd", "getlastmsgid")
                .GET();
        if (client.isSuccess()) {
            JsonObject data = client.getData();
            return data.get("msg_id").getAsLong();
        }
        return 0;
    }
}
