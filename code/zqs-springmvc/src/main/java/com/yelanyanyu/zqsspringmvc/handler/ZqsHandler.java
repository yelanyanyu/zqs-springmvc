package com.yelanyanyu.zqsspringmvc.handler;

import java.lang.reflect.Method;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ZqsHandler {
    private String url;
    private Object controller;
    private Method method;

    public ZqsHandler() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ZqsHandler(String url, Object controller, Method method) {
        this.url = url;
        this.controller = controller;
        this.method = method;
    }
}
