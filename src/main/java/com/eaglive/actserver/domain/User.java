package com.eaglive.actserver.domain;

import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.lib.JsonInfo;
import com.eaglive.actserver.redis.RedisExecManager;

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
        userHash = "";
        token = "";
        nickName = "";
        headPhoto = "";
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
                ", nickName='" + nickName + '\'' +
                ", headPhoto='" + headPhoto + '\'' +
                '}';
    }

    private String parseHeadPhoto(String headPhotoName) {
        return ConfigData.apiUrl + "images/" +  headPhotoName;
    }

    private boolean isMember() {
        String key = "usertoken:" + this.userHash;
        JsonInfo memberInfo = getRedisValue(key);

        if(memberInfo.getInt("state") == 0) {
            return false;
        }
        return true;
    }

    private JsonInfo getRedisValue(String key) {
        return RedisExecManager.instance().getJsonInfo(key);
    }

    private boolean hasRedisKey(String key) {
        return RedisExecManager.instance().exsit(key);
    }

    private boolean isWeixin() {
        String key = "wxtouristinfo:" + userHash;
        return hasRedisKey(key);
    }

    private void getMemberUserInfo() {
        String key = "userdata:"  + userHash;
        JsonInfo userData = getRedisValue(key);
        JsonInfo userinfo = userData.getJsonInfo("userinfo");
        if(userinfo != null) {
            String headPhotoName = userinfo.getString("headPhoto");
            this.headPhoto = parseHeadPhoto(headPhotoName);
            this.nickName = userinfo.getString("nickname");
        }
    }

    private void getWeixinUserInfo() {
        String key = "wxtouristinfo:" + userHash;
        JsonInfo weixinInfo = getRedisValue(key);

        if(weixinInfo != null) {
            this.nickName =  weixinInfo.getString("nickname");
            this.headPhoto = weixinInfo.getString("headimgurl");
        }
    }
    private void getTouristUserInfo() {
        String key = "touristinfo:" + userHash;
        JsonInfo touristInfo = getRedisValue(key);
        if(touristInfo != null) {
            this.nickName = touristInfo.getString("nickname");
            this.headPhoto = touristInfo.getString("headimgurl");
        }
    }
}
