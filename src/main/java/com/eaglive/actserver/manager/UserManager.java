package com.eaglive.actserver.manager;

import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.message.response.OfflineMessage;
import com.eaglive.actserver.message.response.ResponseMessage;
import com.eaglive.actserver.util.ServerWriter;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by admin on 2015/11/9.
 */
public class UserManager {
    public static final UserManager instance = new UserManager();
    private final Map<Channel, User> ctx2User = new ConcurrentHashMap<Channel, User>();
    private final Map<String, Channel> user2Ctx = new ConcurrentHashMap<String, Channel>();

    public User getUser(Channel channel) {
        return this.ctx2User.get(channel);
    }
    public Channel getChannel(User user) {
        return getChannel(user.getUserHash());
    }

    public Channel getChannel(String userHash) {
        return this.user2Ctx.get(userHash);
    }

    public void loginAndKickLastChannel(User user, Channel channel) {
        Channel lastChannel = this.user2Ctx.get(user.getUserHash());
        if(lastChannel != null) {
            kickChannel(lastChannel);
        }
        this.ctx2User.put(channel, user);
        this.user2Ctx.put(user.getUserHash(), channel);
    }

    private void kickChannel(Channel channel) {
        ResponseMessage offlineMessage = new OfflineMessage();
        ServerWriter.writeAndClose(channel, offlineMessage);
    }
}