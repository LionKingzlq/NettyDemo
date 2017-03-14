package com.abraham.netty.v1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Abraham on 2017/2/27.
 */
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf message;

    public ClientChannelHandler(){
        byte[] req = ("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
        message = Unpooled.buffer(req.length);
        message.writeBytes(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        如果执行super.read，则消息被消耗掉了
//        super.channelRead(ctx, msg);

//        ByteBuf buf = (ByteBuf) msg;
//        byte[] respBytes = new byte[buf.readableBytes()];
//        buf.readBytes(respBytes);
//        String body = new String(respBytes, "UTF-8");
        System.out.print(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
