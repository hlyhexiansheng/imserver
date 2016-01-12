package com.eaglive.actserver.message.request;

/**
 * Created by admin on 2015/11/10.
 */
public class LoginMessage extends RequestMessage {
    public String userHash;
    public String token;
    @Override
    protected void parse() {
        this.userHash = this.buffer.readString();
        this.token = this.buffer.readString();
    }
}
