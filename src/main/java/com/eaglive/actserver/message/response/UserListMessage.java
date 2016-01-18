package com.eaglive.actserver.message.response;

import com.eaglive.actserver.config.Code;
import com.eaglive.actserver.config.Command;

import java.util.List;

/**
 * Created by admin on 2016/1/18.
 */
public class UserListMessage extends ResponseMessage{
    public String channel;
    public List<String> userlist;
    public UserListMessage() {
        this.cmd = Command.ONLINE_USER_LIST;
        this.code = Code.OK;
    }
}
