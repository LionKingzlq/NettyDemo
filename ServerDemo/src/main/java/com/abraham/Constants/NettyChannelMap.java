package com.abraham.Constants;

import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Abraham on 2017/3/16.
 */
public class NettyChannelMap {
    private static Map<String, SocketChannel> map = new ConcurrentHashMap<String, SocketChannel>();

    public static void add(String clientId, SocketChannel clientChannel){
        map.put(clientId,clientChannel);
    }

    public static SocketChannel get(String clientId){
        return map.get(clientId);
    }

    public static void remove(SocketChannel clientChannel){
        for (Map.Entry entry : map.entrySet()){
            if(entry.getValue() == clientChannel){
                map.remove(entry.getKey());
            }
        }
    }

    public static boolean containsKey(String clientId){
        return map.containsKey(clientId);
    }
}
