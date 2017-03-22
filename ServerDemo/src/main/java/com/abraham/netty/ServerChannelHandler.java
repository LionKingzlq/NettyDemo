package com.abraham.netty;

import com.abraham.Constants.NettyChannelMap;
import com.abraham.Constants.NettyMethodMap;
import com.abraham.project.controller.OrgAdminController;
import com.abraham.project.model.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AsciiString;
import net.sf.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.HOST;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by Abraham on 2017/2/27.
 */
public class ServerChannelHandler extends SimpleChannelInboundHandler<Object> {

    // websocket 服务的 uri
    private static final String WEBSOCKET_PATH = "/websocket";

    private WebSocketServerHandshaker handshaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest)msg;
            handleHttp(ctx,request);
            System.out.print(request.toString());
        }else if(msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }

    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        Channel incoming = ctx.channel();
        System.out.println("收到" + incoming.remoteAddress() + " 握手请求");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

//        String channelId = ctx.channel().id().asShortText();
//
//        if (loginClientMap.containsKey(channelId)) {
//            loginClientMap.remove(channelId);
//        }
//
//        if (client != null && channelGroupMap.containsKey(client.getRoomId())) {
//            channelGroupMap.get(client.getRoomId()).remove(ctx.channel());
//        }
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

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        broadcast(ctx, frame);
    }

    private void broadcast(ChannelHandlerContext ctx, WebSocketFrame frame) {

        if (!NettyChannelMap.containsKey(ctx.channel().id().asShortText())) {
            Response response = new Response(1001, "没登录不能聊天哦");
            String msg = JSONObject.fromObject(response).toString();
            ctx.channel().write(new TextWebSocketFrame(msg));
            return;
        }

        String request = ((TextWebSocketFrame) frame).text();
        System.out.println(" 收到 " + ctx.channel() + request);


//        Response response = MessageService.sendMessage(client, request);
//        String msg = JSONObject.fromObject(response).toString();
//        if (channelGroupMap.containsKey(client.getRoomId())) {
//            channelGroupMap.get(client.getRoomId()).writeAndFlush(new TextWebSocketFrame(msg));
//        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }
}