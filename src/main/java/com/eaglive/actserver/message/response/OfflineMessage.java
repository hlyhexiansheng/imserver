package com.eaglive.actserver.message.response;

import com.eaglive.actserver.config.Code;
import com.eaglive.actserver.config.Command;

/**
 * Created by admin on 2015/11/12.
 */
public class OfflineMessage extends ResponseMessage {
    public OfflineMessage() {
        this.cmd = Command.OFFLINE;
        this.code = Code.OK;
    }
}
