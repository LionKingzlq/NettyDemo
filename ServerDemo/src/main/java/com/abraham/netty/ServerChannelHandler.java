package com.abraham.netty;

import com.abraham.Constants.NettyChannelMap;
import com.abraham.Constants.NettyMethodMap;
import com.abraham.project.controller.OrgAdminController;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.AsciiString;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by Abraham on 2017/2/27.
 */
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest)msg;
            handleHttp(ctx,request);
            System.out.print(request.toString());
        }

        ctx.writeAndFlush("dasda");
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


    public void handleHttp(ChannelHandlerContext ctx, HttpRequest request){

        Map<String, Object> params = new HashMap<String, Object>();

        if(request.method().equals(HttpMethod.GET)){
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            for (Map.Entry entry : decoder.parameters().entrySet()){
                params.put(entry.getKey().toString(), entry.getValue());
            }

            Method method = NettyMethodMap.getGetMethod(request.uri());
            try {
                method.invoke(params);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(request.method().equals(HttpMethod.POST)){
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

            for (InterfaceHttpData parm : parmList) {
                Attribute data = (Attribute) parm;
                try {
                    params.put(data.getName(),data.getValue());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            Method method = NettyMethodMap.getPostMethod(request.uri());
            try {
                Object orgAdminController = OrgAdminController.class.newInstance();
                Object result = method.invoke(orgAdminController,params);
                ctx.writeAndFlush(request);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        byte[] req = "<div>success</div>".getBytes();
        ByteBuf message = Unpooled.buffer(req.length);
        message.writeBytes(req);

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, message);

        response.headers().set(CONTENT_TYPE,new AsciiString("application/json; charset=utf-8"));
        response.headers().set(TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        if (true) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
        }

    }
}