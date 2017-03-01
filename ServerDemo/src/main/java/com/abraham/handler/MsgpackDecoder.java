package com.abraham.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;


/**
 * Created by Abraham on 2017/3/1.
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf arg1, List<Object> list) throws Exception {
        final  byte[] array;
        final int length =arg1.readableBytes();
        array = new byte[length];

        arg1.getBytes(arg1.readerIndex(),array,0,length);
        MessagePack messagePack = new MessagePack();
        list.add(messagePack.read(array));
    }
}
