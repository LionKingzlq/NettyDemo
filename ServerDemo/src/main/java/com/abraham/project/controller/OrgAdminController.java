package com.abraham.project.controller;

import com.abraham.Constants.NettyChannelMap;
import com.abraham.project.javacommon.BaseController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Created by Abraham on 2017/3/17.
 */
@Component
@RequestMapping(value = "/orgAdmin")
public class OrgAdminController extends BaseController{

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JSONObject login(Map params){
        JSONObject result = new JSONObject();

        return result;
    }


}
