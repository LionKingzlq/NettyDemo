package com.abraham.Constants;

import com.abraham.project.javacommon.BaseController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Abraham on 2017/3/17.
 */
public class NettyControllerMap {
    private static Map<String, BaseController> map = new ConcurrentHashMap<String, BaseController>();

}
