package com.eaglive.actserver;

import com.eaglive.actserver.handler.SystemHandler;
import com.eaglive.actserver.handler.WebSocketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by admin on 2015/11/9.
 */
public class ActChannelInitializer extends ChannelInitializer<Channel> {

    private SystemHandler systemHandler = new SystemHandler();

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());
        pipeline.addLast(new WebSocketHandler());
        pipeline.addLast(this.systemHandler);
    }
}
