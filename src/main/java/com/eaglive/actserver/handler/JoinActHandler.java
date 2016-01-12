package com.eaglive.actserver.handler;

import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ActivityManager;
import com.eaglive.actserver.manager.UserManager;
import com.eaglive.actserver.message.response.JoinMessage;
import com.eaglive.actserver.util.ServerWriter;

/**
 * Created by admin on 2015/11/12.
 */
public class JoinActHandler extends BaseHandler {
    @Override
    protected void run() {
        String hash = this.getStringParam("channel");
        Activity activity = ActivityManager.instance.getActivityOrCreateNew(hash);
        User user = UserManager.instance.getUser(this.channel);
        ActivityManager.instance.joinActivity(activity, user);

        JoinMessage joinMessage = new JoinMessage();
        joinMessage.channel = hash;
        ServerWriter.write(channel, joinMessage);
        System.out.println("Join oK");
        System.out.println("User:"+user+" \nhas joined " + activity);
    }
}
