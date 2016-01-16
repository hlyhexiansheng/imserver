package com.eaglive.actserver.handler;

import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ActivityManager;
import com.eaglive.actserver.manager.UserManager;

/**
 * Created by admin on 2015/11/26.
 */
public class LoveHandler extends BaseHandler {
    @Override
    protected void run() {
        long num = getLongParam("num");
        User user = UserManager.instance.getUser(this.channel);
        Activity activity = ActivityManager.instance.getUserActivity(user);

        if(activity != null) {
            activity.addLoveNumber(num);
        }
    }
}
