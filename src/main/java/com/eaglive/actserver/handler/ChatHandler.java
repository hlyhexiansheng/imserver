package com.eaglive.actserver.handler;

import com.eaglive.actserver.ActServer;
import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ActivityManager;
import com.eaglive.actserver.manager.UserManager;
import com.eaglive.actserver.message.response.ChatMessage;
import com.eaglive.actserver.task.WriteCommentTask;
import com.eaglive.actserver.util.Badword;
import com.eaglive.actserver.util.ServerWriter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2015/11/26.
 */
public class ChatHandler extends BaseHandler {
    @Override
    protected void run() {
        String word = this.getStringParam("data");
        User user = UserManager.instance.getUser(this.channel);
        Activity activity = ActivityManager.instance.getUserActivity(user);

        System.out.println("chat" + word);
        if (activity != null) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.msgId = ConfigData.nextMsgId();
            chatMessage.data = Badword.instance.filter(word);
            chatMessage.channel = activity.getHash();
            chatMessage.headPhoto = user.getHeadPhoto();
            chatMessage.nickname = user.getNickName();
            chatMessage.userHash = user.getUserHash();
            chatMessage.time = System.currentTimeMillis() / 1000;
            chatMessage.readtime = getReadTime();
            addComment(chatMessage);

            ServerWriter.writeToActivityButHimself(activity, user, chatMessage);
        }
    }

    private void addComment(ChatMessage chatMessage) {
        Runnable task = new WriteCommentTask(chatMessage);
        new Thread(task).start();
      //  ActServer.server.submitTask(task);
    }

    private String getReadTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}
