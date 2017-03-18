package com.abraham.Constants;

import io.netty.channel.socket.SocketChannel;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Abraham on 2017/3/17.
 */
public class NettyMethodMap {
    private static Map<String, Method> postMap = new ConcurrentHashMap<String, Method>();
    private static Map<String, Method> getMap = new ConcurrentHashMap<String, Method>();

    public static void addGetMethod(String url, Method method){
        getMap.put(url,method);
    }
    public static Method getGetMethod(String url){
        return getMap.get(url);
    }


    public static void addPostMethod(String url, Method method){
        postMap.put(url,method);
    }
    public static Method getPostMethod(String url){
        return postMap.get(url);
    }
}