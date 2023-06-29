package com.yelanyanyu.zqsspringmvc.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yelanyanyu.zqsspringmvc.annotation.RequestMapping;
import com.yelanyanyu.zqsspringmvc.annotation.RequestParam;
import com.yelanyanyu.zqsspringmvc.annotation.ResponseBody;
import com.yelanyanyu.zqsspringmvc.handler.ZqsHandler;
import com.yelanyanyu.zqsspringmvc.webcontext.ZqsWebApplicationContext;
import com.yelanyanyu.zqsspringmvc.xml.XmlParser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ZqsDispatcherServlet extends HttpServlet {
    private static final String REDIRECT = "redirect";
    private static final String FORWARD = "forward";
    /**
     * key: uri, /zqs_springmvc/aa/  ;
     * val: handler
     */
    private final Map<String, ZqsHandler> handlerMap = new HashMap<>();
    private ZqsWebApplicationContext ioc;

    @Override
    public void init(ServletConfig config) throws ServletException {
        String springConfigLocation = config.getInitParameter("springConfigLocation");
        String loc = springConfigLocation.split(":")[1];
        ioc = new ZqsWebApplicationContext(XmlParser.getBasePackage(loc));
        initHandlerMap(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        requestHandler(req, resp);
    }

    /**
     * 完成请求分发，调用相应的方法
     *
     * @param request
     * @param response
     */
    private void requestHandler(HttpServletRequest request, HttpServletResponse response) {
        //形如 /zqs_springmvc/aa/
        String uri = request.getRequestURI();
        ZqsHandler handler = getHandler(uri);
        try {
            if (handler == null) {
                PrintWriter writer = response.getWriter();
                writer.println("404 NOT FOUND");
                writer.flush();
                writer.close();
            } else {
                //如果找到了对应的handler,调用方法,传递参数,接受返回值进行处理
                Method method = handler.getMethod();
                Parameter[] parameters = method.getParameters();
                Object[] args = new Object[parameters.length];
                Map<String, String[]> requestParameterMap = request.getParameterMap();
                //先装配req 和 resp
                Class<?> type;
                for (int i = 0; i < parameters.length; i++) {
                    type = parameters[i].getType();
                    if (type.equals(HttpServletRequest.class)) {
                        args[i] = request;
                    } else if (type.equals(HttpServletResponse.class)) {
                        args[i] = response;
                    }
                }

                request.setCharacterEncoding("utf-8");
                //装配其他参数
                for (Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
                    String reqParamName = entry.getKey();
                    String[] reqParamVal = entry.getValue();
                    int indexOfParam = -1;
                    String paramName;
                    for (int i = 0; i < parameters.length; i++) {
                        paramName = parameters[i].getName();
                        if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                            paramName = parameters[i].getAnnotation(RequestParam.class).value();
                        }
                        if (reqParamName.equals(paramName)) {
                            indexOfParam = i;
                            break;
                        }
                    }
                    if (indexOfParam == -1) {
                        throw new IllegalArgumentException("参数(" + reqParamName + ")不匹配");
                    }
                    args[indexOfParam] = matchType(parameters[indexOfParam], reqParamVal[0]);
                }

                Object result = handler.getMethod().invoke(handler.getController(), args);

                if (result instanceof String) {
                    //调用相关的资源
                    String[] split = ((String) result).split(":");
                    if (split[0].equalsIgnoreCase(FORWARD)) {
                        request.getRequestDispatcher(split[1]).forward(request, response);
                    } else if (split[0].equalsIgnoreCase(REDIRECT)) {
                        response.sendRedirect(request.getContextPath() + split[1]);
                    } else {
                        request.getRequestDispatcher((String) result).forward(request, response);
                    }
                } else { //返回的是JSON数据, 暂不进行处理,直接打印
                    if (!method.isAnnotationPresent(ResponseBody.class)) {
                        throw new RuntimeException("参数传递错误");
                    }
                    String s = new ObjectMapper().writeValueAsString(result);
                    response.setContentType("text/html;charset=utf-8");
                    response.getWriter().println(s);
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object matchType(Parameter parameter, String val) {
        if (parameter.getType().equals(String.class)) {
            return val;
        } else if (parameter.getType().equals(Integer.class)) {
            return Integer.valueOf(val);
        } else if (parameter.getType().equals(BigDecimal.class)) {
            return BigDecimal.valueOf(Long.valueOf(val));
        } else {
            throw new RuntimeException("类型转换错误");
        }
    }

    /**
     * 根据uri得到相应的Handler
     *
     * @param uri
     * @return
     */
    private ZqsHandler getHandler(String uri) {
        if (handlerMap.isEmpty()) {
            return null;
        }
        return handlerMap.get(uri);
    }

    /**
     * 初始化handlerMap
     */
    private void initHandlerMap(ServletConfig config) {
        if (ioc.getIoc().isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.getIoc().entrySet()) {
            //得到所有方法请求的url(@RequestMapping)
            Object bean = entry.getValue();
            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                String url = "";
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                    url = annotation.value();
                }
                handlerMap.put(config.getServletContext().getContextPath() + url,
                        new ZqsHandler(url, bean, method));
            }
        }
    }


}
