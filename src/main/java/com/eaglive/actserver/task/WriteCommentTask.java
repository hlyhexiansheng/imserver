package com.eaglive.actserver.task;

import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.message.response.ChatMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.*;

/**
 * Created by admin on 2015/12/1.
 */
public class WriteCommentTask implements Runnable {

    private final ChatMessage chatMessage;
    private final OkHttpClient httpClient = new OkHttpClient();
    public WriteCommentTask(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
    public void run() {
        try {
            String bodyString = parseToJson();

            String url = ConfigData.apiUrl + "?cmd=phoneaddcomment";
            MediaType mediaType = MediaType.parse("text/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, bodyString);
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = httpClient.newCall(request).execute();
            System.out.println(response.body().string());

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

        JsonArray array = new JsonArray();
        array.add(jsonObject);
        return array.toString();
    }
}
