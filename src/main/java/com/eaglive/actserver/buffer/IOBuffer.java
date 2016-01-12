package com.eaglive.actserver.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

/**
 * Created by admin on 2015/11/9.
 */
public class IOBuffer {
    private ByteBuf buffer;
    public IOBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }
    public ByteBuf getByteBuf() {
        return this.buffer;
    }

    public short readShort() {
        return buffer.readShort();
    }
    public int readInt() {
        return buffer.readInt();
    }
    public byte[] readBytes(int length) {
        return this.buffer.readBytes(length).array();
    }
    public String readString() {
        int byteSize = readInt();
        System.out.println("size is " + byteSize);
        ByteBuf stringBuf = buffer.readBytes(buffer, byteSize);
        System.out.println(stringBuf.toString(CharsetUtil.UTF_8));
        return stringBuf.toString(CharsetUtil.UTF_8);
    }

}
