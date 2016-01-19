package com.eaglive.actserver.util;

import com.eaglive.actserver.db.DBManager;
import com.eaglive.actserver.lib.JsonInfo;
import com.eaglive.actserver.redis.RedisExecManager;

/**
 * Created by admin on 2016/1/16.
 */
public class ChannelUtil {

    public static JsonInfo getChannelInfo(String channelHash) {
        String key = "channel:" + channelHash;
        return RedisExecManager.instance().getJsonInfo(key);
    }

    public static String getChannelOwner(String channelHash) {
        JsonInfo channelInfo = getChannelInfo(channelHash);
        if(channelInfo == null) {
            return "";
        }
        return channelInfo.getString("userhash");
    }
    public static boolean isChannelLive(String channelHash) {
        JsonInfo channelInfo = getChannelInfo(channelHash);
        if(channelInfo == null) {
            return false;
        }
        return channelInfo.getInt("state") == 1;
    }


    public static void endChannel(String channelHash) {
        String key = "channel:" + channelHash;
        RedisExecManager.instance().delete(key);

        String sql = "update channel set state=2 where hash=?";
        DBManager.cloudLiveDB().executeCommand(sql, new Object[]{channelHash});
    }
}
