package com.eaglive.actserver.handler;

import com.eaglive.actserver.ActServer;
import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ActivityManager;
import com.eaglive.actserver.manager.UserManager;
import com.eaglive.actserver.message.response.ChatMessage;
import com.eaglive.actserver.redis.RedisExecManager;
import com.eaglive.actserver.task.WriteCommentTask;
import com.eaglive.actserver.util.Badword;
import com.eaglive.actserver.util.BaseUtil;
import com.eaglive.actserver.util.ServerWriter;

/**
 * Created by admin on 2015/11/26.
 */
public class ChatHandler extends BaseHandler {
    @Override
    protected void run() {
        String word = this.getStringParam("data");
        User user = UserManager.instance.getUser(this.channel);
        Activity activity = ActivityManager.instance.getUserActivity(user);

        if(activity == null || isClosedChannel(activity.getHash())) {
            System.out.println("is closed chanel");
            return;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.msgId = ConfigData.nextMsgId();
        chatMessage.data = Badword.instance.filter(word);
        chatMessage.channel = activity.getHash();
        chatMessage.headPhoto = user.getHeadPhoto();

        String filterNickName = user.getNickName();
        if(filterNickName.length() > 30) {
            filterNickName = filterNickName.substring(0, 30);
        }
        chatMessage.nickname = filterNickName;
        chatMessage.userHash = user.getUserHash();
        chatMessage.time = BaseUtil.timestamp();
        chatMessage.readtime = BaseUtil.getReadTime();
        addComment(chatMessage);

        ServerWriter.writeToActivityButHimself(activity, user, chatMessage);

    }

    private void addComment(ChatMessage chatMessage) {
        Runnable task = new WriteCommentTask(chatMessage);
        ActServer.server.submitTask(task);
    }

    private boolean isClosedChannel(String hash) {
        String key = "closed_channel_list";
        return RedisExecManager.instance().sContain(key, hash);
    }
}
