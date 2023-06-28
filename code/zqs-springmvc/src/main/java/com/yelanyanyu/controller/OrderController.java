package com.yelanyanyu.controller;

import com.yelanyanyu.zqsspringmvc.annotation.Controller;
import com.yelanyanyu.zqsspringmvc.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Controller
public class OrderController {
    @RequestMapping(value = "/order/test01")
    public String test01(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("test01...");
        return "";
    }
}
