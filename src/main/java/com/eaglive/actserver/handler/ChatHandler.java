package com.eaglive.actserver.handler;

import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;
import com.eaglive.actserver.manager.ActivityManager;
import com.eaglive.actserver.manager.UserManager;
import com.eaglive.actserver.message.response.ChatMessage;
import com.eaglive.actserver.task.WriteCommentTask;
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

        System.out.println("chat" + word);
        if (activity != null) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.msgId = ConfigData.nextMsgId();
            chatMessage.data = word;
            chatMessage.channel = activity.getHash();
            chatMessage.headPhoto = user.getHeadPhoto();
            chatMessage.nickname = user.getNickName();
            addComment(chatMessage);

            ServerWriter.writeToActivityButHimself(activity, user, chatMessage);
        }
    }

    private void addComment(ChatMessage chatMessage) {
        System.out.println("*********************");
        new Thread(new WriteCommentTask(chatMessage)).start();
    }
}
