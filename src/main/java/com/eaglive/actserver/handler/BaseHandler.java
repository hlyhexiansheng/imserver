package com.eaglive.actserver.handler;

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
        return this.request.get(key).getAsString();
    }
    protected int getIntParam(String key) {
        return this.request.get(key).getAsInt();
    }
    protected long getLongParam(String key) {
        return this.request.get(key).getAsLong();
    }
}
