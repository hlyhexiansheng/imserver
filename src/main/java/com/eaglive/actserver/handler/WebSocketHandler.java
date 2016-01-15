package com.eaglive.actserver.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

/**
 * Created by admin on 2015/11/9.
 */
public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            processHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            processWebSocketRequest(ctx, (WebSocketFrame) msg);
        }
    }

    private void processHttpRequest(ChannelHandlerContext ctx, HttpRequest request) throws InterruptedException {
        if (!HttpMethod.GET.equals(request.getMethod())) {
            sendBadRequest(ctx);
            return;
        }

        HttpHeaders headers = request.headers();
        String upgrade = headers.get("Upgrade");
        if (upgrade == null || !upgrade.equalsIgnoreCase("websocket")) {
            sendBadRequest(ctx);
            return;
        }

        String wsHost = "ws://"+request.headers().get(HttpHeaders.Names.HOST);
        System.out.println(wsHost);
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
                wsHost, null, false);
        WebSocketServerHandshaker handshaker = factory.newHandshaker(request);
        if (handshaker == null) {
            factory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }
    }

    void processWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame request){
        if (request instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) request;
            JsonParser parser = new JsonParser();
            String text = textFrame.text();
            JsonObject jsonObject = parser.parse(text).getAsJsonObject();
            System.out.println("I Got: " + text);
            if (jsonObject != null) {
                ctx.fireChannelRead(jsonObject);
            }
        } else {
            ctx.close();
        }
    }

    private void sendBadRequest(ChannelHandlerContext ctx) {
        DefaultHttpResponse response = new DefaultHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
        ChannelFuture future = ctx.writeAndFlush(response);
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
