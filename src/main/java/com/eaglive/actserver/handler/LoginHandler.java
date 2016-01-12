package com.eaglive.actserver.handler;

import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ChannelManager;
import com.eaglive.actserver.manager.UserManager;
import com.eaglive.actserver.message.response.LoginMessage;
import com.eaglive.actserver.util.ServerWriter;
import com.eaglive.actserver.util.UserUtil;

/**
 * Created by admin on 2015/11/12.
 */
public class LoginHandler extends BaseHandler {
    @Override
    protected void run() {
        String userHash = this.getStringParam("userhash");
        String token = this.getStringParam("token");

        if (!UserUtil.verifyUser(userHash, token)) {
            ChannelManager.instance.lostConn(channel,
                    ConfigData.LOST_CHANNEL_INVALID_TOKEN);
            return;
        }
        User user = new User(userHash, token);
        user.refreshUserInfo();
        UserManager.instance.loginAndKickLastChannel(user, channel);

        LoginMessage loginMessage = new LoginMessage();
        loginMessage.userhash = userHash;
        ServerWriter.write(channel, loginMessage);
        System.out.println("Login oK");
    }
}
