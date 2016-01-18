package com.eaglive.actserver.message.response;

import com.eaglive.actserver.config.Code;
import com.eaglive.actserver.config.Command;

/**
 * Created by admin on 2016/1/18.
 */
public class NotificationMessage extends ResponseMessage {
    public long id;
    public String type;
    public String title;
    public String content;
    public String extra;
    public NotificationMessage() {
        this.cmd = Command.NOTIFICATION;
        this.code = Code.OK;
    }
}
