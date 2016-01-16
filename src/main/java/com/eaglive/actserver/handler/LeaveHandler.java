package com.eaglive.actserver.handler;

import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ActivityManager;
import com.eaglive.actserver.manager.UserManager;

/**
 * Created by admin on 2015/11/26.
 */
public class LeaveHandler extends BaseHandler {
    @Override
    protected void run() {
        String hash = this.getStringParam("channel");
        User user = UserManager.instance.getUser(this.channel);
        Activity activity = ActivityManager.instance.getActivityOrCreateNew(hash, user);
        ActivityManager.instance.leaveActivity(activity, user);
        System.out.println("User:"+user+" \nhas joined " + activity);
    }
}
