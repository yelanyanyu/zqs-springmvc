package com.yelanyanyu.zqsspringmvc.webcontext;

import java.io.File;
import java.util.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ZqsWebApplicationContext {
    /**
     * 暂时只存储单例对象.
     * key:对象实例的名字
     * value:对象实例
     */
    private final Map<String, Object> ioc = new HashMap<>();
    /**
     * 各个类的全路径,用于反射生成类实例
     */
    private final List<String> classFullPaths = new ArrayList<>();
    /**
     * 待扫描的包名
     */
    private String basePackage;

    public ZqsWebApplicationContext(String basePackage) {
        this.basePackage = basePackage;
        init();
    }

    /**
     * 完全容器的初始化
     */
    public void init() {
        String[] packages = basePackage.split(",");
        for (String p : packages) {
            scanPackage(p);
        }
        //注入单例对象
        injectSingleObjs();
    }


    /**
     * 根据basePackage扫描所有的包加入classFullPaths.
     *
     * @param basePackage 形如com.yelanyanyu.service
     */
    public void scanPackage(String basePackage) {
        String absPath = ZqsWebApplicationContext.class.getClassLoader().getResource(basePackage.replace('.', '/')).getFile();
        File rootFile = new File(absPath);
        Stack<File> stack = new Stack<>();
        stack.push(rootFile);
        while (!stack.isEmpty()) {
            File cur = stack.pop();
            for (File file : cur.listFiles()) {
                if (file.isDirectory()) {
                    stack.push(file);
                } else {
                    //类似于这种路径:D:\myCode\formal-projects\zqs-springmvc\code\zqs-springmvc\target\zqs_springmvc\WEB-INF\classes\com\yelanyanyu\service\MonsterService.class
                    String absolutePath = file.getAbsolutePath();
                    String base = ZqsWebApplicationContext.class.getClassLoader().getResource("").getFile();
                    absolutePath = absolutePath.replace(base.substring(1).replace('/', '\\'), "");
                    classFullPaths.add(absolutePath.replace('\\', '.').replaceAll(".class", ""));
                }
            }
        }
    }

    /**
     * 根据classFullPaths，通过反射生成对象实例加入ioc中
     */
    public void injectSingleObjs() {

    }
}
