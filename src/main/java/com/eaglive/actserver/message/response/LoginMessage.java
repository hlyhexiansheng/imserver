package com.eaglive.actserver.message.response;

import com.eaglive.actserver.config.Code;
import com.eaglive.actserver.config.Command;

/**
 * Created by admin on 2015/11/12.
 */
public class LoginMessage extends ResponseMessage {
    public String userhash;
    public LoginMessage() {
        this.cmd = Command.LOGIN;
        this.code = Code.OK;
    }
}
