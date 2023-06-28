package com.yelanyanyu.zqsspringmvc.webcontext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ZqsWebApplicationContext {
    private final Map<String, Object> ioc = new HashMap<>();
    private String basePackage;


    public ZqsWebApplicationContext(String basePackage) {
        this.basePackage = basePackage;
        init();
    }

    public void init() {

    }

    public void injectSingleObjs() {

    }
}
