package com.yelanyanyu.zqsspringmvc.webcontext;

import com.yelanyanyu.zqsspringmvc.annotation.Autowired;
import com.yelanyanyu.zqsspringmvc.annotation.Controller;
import com.yelanyanyu.zqsspringmvc.annotation.Service;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
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

    public Map<String, Object> getIoc() {
        return ioc;
    }

    public List<String> getClassFullPaths() {
        return classFullPaths;
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
        executeAutowired();
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
        if (classFullPaths.isEmpty()) {
            return;
        }
        //需要加入ioc的类:被Controller、Service修饰的类
        try {
            for (String classFullPath : classFullPaths) {
                Class<?> aClass = Class.forName(classFullPath);
                if (aClass.isAnnotationPresent(Controller.class)) {
                    Controller annotation = aClass.getAnnotation(Controller.class);
                    String name = annotation.value();
                    if (StringUtils.isEmpty(name)) {
                        name = StringUtils.uncapitalize(aClass.getSimpleName());
                    }
                    ioc.put(name, aClass.newInstance());
                } else if (aClass.isAnnotationPresent(Service.class)) {
                    Service annotation = aClass.getAnnotation(Service.class);
                    String name = annotation.value();
                    if (StringUtils.isEmpty(name)) {
                        Object o = aClass.newInstance();
                        for (Class<?> anInterface : aClass.getInterfaces()) {
                            name = StringUtils.uncapitalize(anInterface.getSimpleName());
                            ioc.put(name, o);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理ioc中的类的自动装配
     */
    public void executeAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        try {
            for (Map.Entry<String, Object> entry : ioc.entrySet()) {
                Object bean = entry.getValue();
                for (Field field : bean.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        field.setAccessible(true);

                        Autowired annotation = field.getAnnotation(Autowired.class);
                        String beanName = annotation.value();
                        if (StringUtils.isEmpty(beanName)) {
                            beanName = StringUtils.uncapitalize(field.getType().getSimpleName());
                        }

                        Object o = ioc.get(beanName);
                        if (o == null) {
                            throw new RuntimeException("装配失败,没有该对象");
                        }

                        field.set(bean, o);

                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
