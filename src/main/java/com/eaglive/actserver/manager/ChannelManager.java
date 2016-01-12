package com.eaglive.actserver.manager;

import io.netty.channel.Channel;

/**
 * Created by admin on 2015/11/9.
 */
public class ChannelManager {
    public static final ChannelManager instance = new ChannelManager();

    public void lostConn(Channel channel, String reason) {
        System.out.println("Lost Conn, reason is : " + reason);
        channel.close();
    }
}
