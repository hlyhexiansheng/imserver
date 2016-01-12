package com.eaglive.actserver.handler;

import com.eaglive.actserver.config.Command;
import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ChannelManager;
import com.eaglive.actserver.manager.UserManager;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2015/11/9.
 */
public class SystemHandler extends ChannelInboundHandlerAdapter {
    private final Map<String, Class> cmd2Handlers;
    public SystemHandler() {
        this.cmd2Handlers = new HashMap<String, Class>();
        this.cmd2Handlers.put(Command.LOGIN, LoginHandler.class);
        this.cmd2Handlers.put(Command.JOIN_ACT, JoinActHandler.class);
        this.cmd2Handlers.put(Command.LEAVE_ROOM, LeaveHandler.class);
        this.cmd2Handlers.put(Command.CHAT_MSG, ChatHandler.class);
        this.cmd2Handlers.put(Command.LOVE, LoveHandler.class);
        this.cmd2Handlers.put(Command.ONLINE_USER_LIST, UserListHandler.class);
        this.cmd2Handlers.put(Command.CLOSE_CHANNEL, CloseHandler.class);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception:" + cause.getMessage());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        JsonObject request = (JsonObject) msg;
        String cmd = request.get("cmd").getAsString();
        Channel channel = ctx.channel();
        do {
            if (!checkCmd(cmd)) {
                this.lostChannel(channel, ConfigData.LOST_CHANNEL_INVALID_CMD);
                break;
            }

            if (!checkCmdAndUser(channel, cmd)) {
                this.lostChannel(channel, ConfigData.LOST_CHANNEL_NOT_LOGIN);
                break;
            }

            BaseHandler handler = getHandler(cmd, request, channel);
            if (handler != null) {
                handler.execute();
            }

        } while (false);
    }

    private boolean checkCmd(String cmd) {
        return this.cmd2Handlers.containsKey(cmd);
    }

    private BaseHandler getHandler(String cmd, JsonObject request, Channel channel) {
        Class handlerClass = this.cmd2Handlers.get(cmd);
        BaseHandler handler = null;
        try {
            handler = (BaseHandler) handlerClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (handler != null) {
            handler.init(request, channel);
        }
        return handler;
    }

    private boolean checkCmdAndUser(Channel channel, String cmd) {
        User user = UserManager.instance.getUser(channel);
        return  (user != null || cmd.equals(Command.LOGIN));
    }

    private void lostChannel(Channel channel, String reason) {
        ChannelManager.instance.lostConn(channel, reason);
    }
}
