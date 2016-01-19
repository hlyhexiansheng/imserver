package com.eaglive.actserver.handler;

import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ActivityManager;
import com.eaglive.actserver.manager.UserManager;
import com.eaglive.actserver.message.response.CloseMessage;
import com.eaglive.actserver.util.ServerWriter;

/**
 * Created by admin on 2015/11/26.
 */
public class CloseHandler extends BaseHandler {
    @Override
    protected void run() {
        String channelHash = this.getStringParam("channel");
        User user = UserManager.instance.getUser(this.channel);
        Activity activity = ActivityManager.instance.getActivityOrCreateNew(channelHash, user);

        if(activity != null) {
            User ownUser = activity.getOwnUser();

            CloseMessage ownCloseMsg = new CloseMessage();
            ownCloseMsg.hash = channelHash;
            ownCloseMsg.record = 1;
            ServerWriter.write(ownUser, ownCloseMsg);

            CloseMessage otherCloseMsg = new CloseMessage();
            otherCloseMsg.record = 0;
            otherCloseMsg.hash = channelHash;
            ServerWriter.writeAllBut(ownUser, activity, otherCloseMsg);
        }
    }
}
