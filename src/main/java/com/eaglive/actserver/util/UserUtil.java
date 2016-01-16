package com.eaglive.actserver.util;

import com.eaglive.actserver.lib.JsonInfo;
import com.eaglive.actserver.redis.RedisExecManager;

/**
 * Created by admin on 2015/11/12.
 */
public class UserUtil {
    public static boolean verifyUser(String userHash, String token) {
        String key = "usertoken:" + userHash;
        JsonInfo tokenInfo = RedisExecManager.instance().getJsonInfo(key);
        String dbUserHash = tokenInfo.getString("userhash");
        String dbToken = tokenInfo.getString("code");
        return (dbUserHash.equals(userHash) && dbToken.equals(token));
    }
}
