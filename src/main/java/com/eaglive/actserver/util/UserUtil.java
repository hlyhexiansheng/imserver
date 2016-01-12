package com.eaglive.actserver.util;

import com.eaglive.actserver.ActServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;

/**
 * Created by admin on 2015/11/12.
 */
public class UserUtil {
    public static boolean verifyUser(String userHash, String token) {
        Jedis jedis = ActServer.server.getJedis();
        try {
            String key = "usertoken:" + userHash;
            String value = jedis.get(key);
            jedis.close();
            if (value != null) {
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(value).getAsJsonObject();
                String dbUserHash = jsonObject.get("userhash").getAsString();
                String dbToken = jsonObject.get("code").getAsString();
                return (dbUserHash.equals(userHash) && dbToken.equals(token));
            }
            return false;
        } finally {
            jedis.close();
        }

    }
}
