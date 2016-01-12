package com.eaglive.actserver.message.response;

import com.eaglive.actserver.config.Code;
import com.eaglive.actserver.config.Command;

/**
 * Created by admin on 2015/11/26.
 */
public class ChatMessage extends ResponseMessage {
    public long msgId;
    public String data;
    public String headPhoto;
    public String channel;
    public String nickname;
    public String time;
    public String readtime;

    public ChatMessage() {
        this.cmd = Command.CHAT_RESPONSE_MSG;
        this.code = Code.OK;
    }
}
