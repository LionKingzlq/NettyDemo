package com.abraham.netty;

import com.abraham.Constants.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import org.json.simple.JSONObject;
import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;

import java.util.Date;

/**
 * Created by Abraham on 2017/2/27.
 */
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

//        ByteBuf buf = (ByteBuf) msg;
//        byte[] respBytes = new byte[buf.readableBytes()];
//        buf.readBytes(respBytes);
//        String body = new String(respBytes, "UTF-8");
        System.out.print(msg);
//        String respString = msg.equals("QUERY TIME ORDER")? new Date().toString() : "Bad Order";
//        ByteBuf resp = Unpooled.buffer(respString.getBytes().length);
//        resp.writeBytes(respString.getBytes());
//        resp.writeBytes((respString + System.getProperty("line.separator")).getBytes());
//        ctx.write(resp);

        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest)msg;

            System.out.println("messageType:" + request.headers().get("messageType"));
            System.out.println("businessType:" + request.headers().get("businessType"));
        }

//        在特定的時候，把socket保存起來
        NettyChannelMap.add("", (SocketChannel) ctx.channel());

    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        String respString = "Bad Order";
        ByteBuf resp = Unpooled.buffer((respString + System.getProperty("line.sepaarator")).getBytes().length);
        resp.writeBytes(respString.getBytes());
        resp.writeBytes((respString + System.getProperty("line.separaator")).getBytes());
        ctx.writeAndFlush(resp);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}