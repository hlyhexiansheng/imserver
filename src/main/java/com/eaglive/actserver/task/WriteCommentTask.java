package com.eaglive.actserver.task;

import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.lib.HttpClient;
import com.eaglive.actserver.message.response.ChatMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.*;

/**
 * Created by admin on 2015/12/1.
 */
public class WriteCommentTask implements Runnable {

    private final ChatMessage chatMessage;
    public WriteCommentTask(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
    public void run() {
        try {
            String bodyString = parseToJson();
            HttpClient httpClient = new HttpClient()
                    .setCmd("phoneaddcomment")
                    .setBody(bodyString);
            httpClient.POST();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String parseToJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("msg_id", this.chatMessage.msgId);
        jsonObject.addProperty("comment", this.chatMessage.data);
        jsonObject.addProperty("channelhash", this.chatMessage.channel);
        jsonObject.addProperty("nick_name", this.chatMessage.nickname);
        jsonObject.addProperty("head_photo", this.chatMessage.headPhoto);
        jsonObject.addProperty("userhash", this.chatMessage.userHash);

        JsonArray array = new JsonArray();
        array.add(jsonObject);
        return array.toString();
    }
}
