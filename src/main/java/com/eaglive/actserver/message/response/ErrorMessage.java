package com.eaglive.actserver.message.response;

import com.eaglive.actserver.config.Command;

/**
 * Created by admin on 2015/11/12.
 */
public class ErrorMessage extends ResponseMessage {
    public String reason;
    public ErrorMessage() {
        this.cmd = Command.ERROR;
    }
}
