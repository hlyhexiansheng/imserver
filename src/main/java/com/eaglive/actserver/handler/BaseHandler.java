package com.eaglive.actserver.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;

/**
 * Created by admin on 2015/11/12.
 */
public abstract class BaseHandler {
    protected JsonObject request;
    protected Channel channel;
    public void init(JsonObject request, Channel channel) {
        this.request = request;
        this.channel = channel;
    }
    public void execute() {
        this.run();
    }
    protected abstract void run();

    protected String getStringParam(String key) {
        JsonElement element = this.request.get(key);
        if(element != null) {
            return element.getAsString();
        }
        return "";
    }
    protected int getIntParam(String key) {
        JsonElement element = this.request.get(key);
        if(element != null) {
            return element.getAsInt();
        }
        return 0;
    }
    protected long getLongParam(String key) {
        JsonElement element = this.request.get(key);
        if(element != null) {
            return element.getAsLong();
        }
        return 0;
    }
}
