package com.eaglive.actserver.handler;

import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ActivityManager;
import com.eaglive.actserver.manager.UserManager;

import java.util.List;

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
            List<User> users = activity.getUsers();
            User ownUser = activity.getOwnUser();
        }
    }
}
