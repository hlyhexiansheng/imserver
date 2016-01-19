package com.eaglive.actserver.handler;

import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ActivityManager;
import com.eaglive.actserver.manager.UserManager;
import com.eaglive.actserver.message.response.UserListMessage;
import com.eaglive.actserver.util.ServerWriter;

import java.util.List;

/**
 * Created by admin on 2015/11/26.
 */
public class UserListHandler extends BaseHandler {
    @Override
    protected void run() {
        User user = UserManager.instance.getUser(this.channel);
        Activity activity = ActivityManager.instance.getUserActivity(user);
        List<String> users = activity.users();

        UserListMessage message = new UserListMessage();
        message.userlist = users;
        message.channel = activity.getHash();
        ServerWriter.write(channel, message);
    }
}
