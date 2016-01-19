package com.eaglive.actserver.util;

import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.UserManager;
import com.eaglive.actserver.message.response.ResponseMessage;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2015/11/12.
 */
public class ServerWriter {

    public static ChannelFuture write(Channel channel, ResponseMessage response) {
        Gson gson = new Gson();
        String msg = gson.toJson(response);
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(msg);
        ChannelFuture future = channel.writeAndFlush(textWebSocketFrame);
        System.out.println("Has write to:" + channel + " msg:" +msg);
        return future;
    }

    public static void writeAndClose(Channel channel, ResponseMessage response) {
        ChannelFuture future = write(channel, response);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    public static void write(User user, ResponseMessage response) {
        ChannelFuture future = write(UserManager.instance.getChannel(user), response);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    public static void writeAllBut(User user, Activity activity, ResponseMessage responseMessage) {
        List<User> users = activity.getUsers();
        List<Channel> channels = new ArrayList<Channel>();
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User other = iterator.next();
            if(user.equals(other)) {
                continue;
            }
            channels.add(UserManager.instance.getChannel(other));
        }
        writeToChannels(channels, responseMessage);
    }

    public static void writeToActivity(Activity activity, ResponseMessage response) {
        List<User> users = activity.getUsers();
        List<Channel> channels = new ArrayList<Channel>();
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User other = iterator.next();
            channels.add(UserManager.instance.getChannel(other));
        }
        writeToChannels(channels, response);
    }

    public static void  writeToActivityButHimself(Activity activity, User user,
                                                  ResponseMessage response) {
        List<User> users = activity.getUsers();
        List<Channel> channels = new ArrayList<Channel>();
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User other = iterator.next();
            if(other.equals(user)) {
                continue;
            }
            channels.add(UserManager.instance.getChannel(other));
        }
        writeToChannels(channels, response);
    }

    private static void writeToChannels(List<Channel> channels, ResponseMessage response) {
        for(int i = 0; i < channels.size(); ++i) {
            write(channels.get(i), response);
        }
    }
}
