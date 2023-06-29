package com.yelanyanyu.zqsspringmvc.servlet;

import com.yelanyanyu.zqsspringmvc.webcontext.ZqsWebApplicationContext;
import com.yelanyanyu.zqsspringmvc.xml.XmlParser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ZqsDispatcherServlet extends HttpServlet {
    private ZqsWebApplicationContext ioc;

    @Override
    public void init(ServletConfig config) throws ServletException {
        String springConfigLocation = config.getInitParameter("springConfigLocation");
        String loc = springConfigLocation.split(":")[1];
        ioc = new ZqsWebApplicationContext(XmlParser.getBasePackage(loc));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private void requestHandler(HttpServletRequest request, HttpServletResponse response) {

    }



}
