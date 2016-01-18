package com.eaglive.actserver.task;


import com.eaglive.actserver.redis.RedisExecManager;
import com.eaglive.actserver.util.BaseUtil;
import com.eaglive.actserver.util.ChannelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by admin on 2015/11/27.
 */
public class ScanTsTask extends TimerTask {

    @Override
    public void run() {
        List<String> needCloseChannels = getNeedCloseChannels();
        closeChannels(needCloseChannels);
    }

    private List<String> getNeedCloseChannels() {
        Map<String, String> allTs = RedisExecManager.instance().hGetAll("channeltslasttime_hash");

        List<String> needCloseChannels = new ArrayList<String>();

        long current = BaseUtil.timestamp();
        for (Map.Entry<String, String> entry : allTs.entrySet()) {
            long time = Long.valueOf(entry.getValue());
            if (current - time < 60) {
                continue;
            }
            String channel = entry.getKey();
            if(isAssociateActivity(channel)) {
                continue;
            }
            needCloseChannels.add(channel);
        }
        return needCloseChannels;
    }

    private boolean isAssociateActivity(String channel) {
        return RedisExecManager.instance().hExist("activity_channel_associate", channel);
    }

    private void closeChannels(List<String> channels) {
        for (String channel : channels) {
            if(ChannelUtil.isChannelLive(channel)) {
                ChannelUtil.endChannel(channel);
            }
        }
    }
}
