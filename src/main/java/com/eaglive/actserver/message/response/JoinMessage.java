package com.eaglive.actserver.message.response;

import com.eaglive.actserver.config.Code;
import com.eaglive.actserver.config.Command;

/**
 * Created by admin on 2015/11/26.
 */
public class JoinMessage extends ResponseMessage {
    public String channel;
    public JoinMessage() {
        this.cmd = Command.JOIN_ACT;
        this.code = Code.OK;
    }
}
