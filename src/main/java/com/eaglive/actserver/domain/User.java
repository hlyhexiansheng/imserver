package com.eaglive.actserver.domain;

import com.eaglive.actserver.ActServer;
import com.eaglive.actserver.config.ConfigData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;

/**
 * Created by admin on 2015/11/9.
 */
public class User {
    private String userHash;
    private String token;

    public String getHeadPhoto() {
        return headPhoto;
    }

    public String getNickName() {
        return nickName;
    }

    private String nickName;
    private String headPhoto;
    public User() {

    }
    public User(String userHash, String token) {
        this.userHash = userHash;
        this.token = token;
        refreshUserInfo();
    }

    public void refreshUserInfo() {
        if(isMember()) {
            getMemberUserInfo();
        } else if(isWeixin()){
            getWeixinUserInfo();
        } else {
            getTouristUserInfo();
        }
    }
    public String getUserHash() {
        return userHash;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int hashCode() {
        return userHash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "userHash='" + userHash + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    private String parseHeadPhoto(String headPhotoName) {
        return ConfigData.apiUrl + "images/" +  headPhotoName;
    }

    private boolean isMember() {
        String key = "usertoken:" + this.userHash;
        JsonObject object = getRedisValue(key);

        if(object.get("state").getAsInt() == 0) {
            return false;
        }
        return true;
    }

    private JsonObject getRedisValue(String key) {
        Jedis jedis = ActServer.server.getJedis();
        String value = jedis.get(key);
        jedis.close();
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(value);
        return object;
    }

    private boolean hasRedisKey(String key) {

        Jedis jedis = ActServer.server.getJedis();
        try {
            return jedis.exists(key);
        } finally {
            jedis.close();
        }
    }

    private boolean isWeixin() {
        String key = "wxtouristinfo:" + userHash;
        return hasRedisKey(key);
    }

    private void getMemberUserInfo() {
        String key = "userdata:"  + userHash;
        JsonObject object = getRedisValue(key);
        JsonObject userinfoObject = (JsonObject) object.get("userinfo");
        String headPhotoName = userinfoObject.get("headPhoto").getAsString();
        this.headPhoto = parseHeadPhoto(headPhotoName);
        this.nickName = userinfoObject.get("nickname").getAsString();
    }

    private void getWeixinUserInfo() {
        String key = "wxtouristinfo:" + userHash;
        JsonObject weixinInfo = getRedisValue(key);

        this.nickName =  weixinInfo.get("nickname").getAsString();
        this.headPhoto = weixinInfo.get("headimgurl").getAsString();
    }
    private void getTouristUserInfo() {
        this.nickName = "游客";
        this.headPhoto = ConfigData.apiUrl + "images/default_head_photo.png";
    }
}
