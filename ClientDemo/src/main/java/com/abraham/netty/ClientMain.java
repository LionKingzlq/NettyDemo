package com.abraham.netty;

import com.abraham.netty.handler.MsgpackDecoder;
import com.abraham.netty.handler.MsgpackEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by Abraham on 2017/2/27.
 */
public class ClientMain {

    public static void main(String[] args) throws Exception{
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (Exception e) {

            }
        }

        new ClientMain().connect(port, "127.0.0.1");
    }

    public void connect(int port, String host) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

//                            socketChannel.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
//                            socketChannel.pipeline().addLast("msgpack encoder",new MsgpackEncoder());

                            socketChannel.pipeline().addLast(new HttpResponseDecoder());
                            socketChannel.pipeline().addLast(new HttpRequestEncoder());

                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new ClientChannelHandler());
                        }
                    });

            ChannelFuture f = b.connect(host,port).sync();
            f.channel().closeFuture().sync();
        }catch (Exception e){

        }finally {
            group.shutdownGracefully();
        }
    }

}
