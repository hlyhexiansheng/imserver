package com.eaglive.actserver.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

/**
 * Created by admin on 2015/11/9.
 */
public class BufferUtil {

    public static String readString(ByteBuf buf) {
        int byteSize = buf.readInt();
        ByteBuf stringBuf = buf.readBytes(buf, byteSize);
        return stringBuf.toString(CharsetUtil.UTF_8);
    }

    public static void writeString(ByteBuf buf, String value) {
        byte []bytes = value.getBytes(CharsetUtil.UTF_8);
        buf.writeInt(value.length());
        buf.writeBytes(bytes);
    }
}
