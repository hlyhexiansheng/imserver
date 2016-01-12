package com.eaglive.actserver.message.request;

import com.eaglive.actserver.buffer.IOBuffer;

/**
 * Created by admin on 2015/11/9.
 */
abstract public class RequestMessage {
    protected IOBuffer buffer;
    public static RequestMessage create(short cmd, IOBuffer buffer) {
        RequestMessage request = null;
        switch (cmd) {
//            case Command.LOGIN:
//                request = new LoginMessage();
//                break;
        }
        if (request!=null) {
            request.buffer = buffer;
            request.parse();
        }
        return request;
    }
    protected abstract void parse();
}
