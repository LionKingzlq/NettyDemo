package com.abraham.project.util;

import com.abraham.Constants.NettyMethodMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;

/**
 * Created by Abraham on 2017/3/17.
 */
public class ReflectUtil {

    public static void reflect(){

        boolean recursive = true;
        String packageName = "com.abraham.project.controller";
        String packageDirName = packageName.replace('.', '/');

        Enumeration<URL> dirs;
        try{
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()){
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    System.err.println("file类型的扫描");
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     */
    public static void findAndAddClassesInPackageByFile( String packageName, String packagePath, final boolean recursive){
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter(){
            public boolean accept(File file){
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirfiles){
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive);
            }else{
                String className = file.getName().substring(0, file.getName().length() - 6);
                try{
                    Class temp = Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className);
                    Annotation annotation = temp.getAnnotation(RequestMapping.class);
                    String[] controllerUrls = new String[]{""};
                    if(annotation != null){
                        RequestMapping requestMapping = (RequestMapping) annotation;
                        if(requestMapping.value().length > 0){
                            controllerUrls = requestMapping.value();
                        }
                    }

                    Method[] methods = temp.getDeclaredMethods();
                    for (Method method : methods){
                        Annotation methodAnnotation = method.getAnnotation(RequestMapping.class);
                        if(methodAnnotation != null){
                            String[] methodUrls = new String[]{""};
                            RequestMapping requestMapping = (RequestMapping) methodAnnotation;
                            if(requestMapping.value().length > 0){
                                methodUrls = requestMapping.value();
                            }

                            for (String conUrl : controllerUrls){
                                for (String methUrl : methodUrls){
                                    if(requestMapping.method().length == 0 || requestMapping.method()[0].equals(RequestMethod.GET)){
                                        NettyMethodMap.addGetMethod(conUrl + methUrl, method);
                                    }else if(requestMapping.method()[0].equals(RequestMethod.POST)){
                                        NettyMethodMap.addPostMethod(conUrl + methUrl, method);
                                    }
                                }
                            }
                        }
                    }
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }
}