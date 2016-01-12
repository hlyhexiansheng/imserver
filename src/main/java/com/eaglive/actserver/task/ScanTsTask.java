package com.eaglive.actserver.task;


import com.eaglive.actserver.ActServer;
import com.eaglive.actserver.util.BaseUtil;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by admin on 2015/11/27.
 */
public class ScanTsTask extends TimerTask {

    @Override
    public void run() {
        //List<String> needCloseChannels = getNeedCloseChannels();
    }

    private List<String> getNeedCloseChannels() {
        Jedis jedis = ActServer.server.getJedis();
        Map<String, String> allTs = jedis.hgetAll("channeltslasttime_hash");
        jedis.close();

        List<String> needCloseChannels = new ArrayList<String>();

        long current = BaseUtil.timestamp();
        for (Map.Entry<String, String> entry : allTs.entrySet()) {
            long time = Long.valueOf(entry.getValue());
            if (current - time < 60) {
                continue;
            }
            String channel = entry.getKey();
            if(isAssociateTask(channel)) {
                continue;
            }
            needCloseChannels.add(channel);
        }
        return needCloseChannels;
    }

    private boolean isAssociateTask(String channel) {
        Jedis jedis = ActServer.server.getJedis();
        try {
            return jedis.hexists("activity_channel_associate", channel);
        } finally {
            jedis.close();
        }
    }
}
