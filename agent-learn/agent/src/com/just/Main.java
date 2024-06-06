package com.just;

import com.sun.tools.attach.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;
import java.util.List;
import java.util.jar.JarFile;

public class Main {
    public static String targetClassName = "com.just.controller.DemoController";
    public static String targetMethodName = "demo";

    public static void main(String[] args) throws AgentLoadException, IOException, AgentInitializationException, AttachNotSupportedException, ClassNotFoundException {
        Class.forName("sun.tools.attach.HotSpotAttachProvider");
        // 获取正在运行 JVM 列表
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        VirtualMachine virtualMachine = VirtualMachine.attach(args[0]);
        String path = getJarFileByClass(Main.class);
        System.out.println("load " + path);
        virtualMachine.loadAgent(path, "com.just.AgentDemoApplication");
        virtualMachine.detach();
    }
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Exception {
        premain(agentArgs, instrumentation);
    }
    public static void premain(String agentArgs, Instrumentation instrumentation) throws Exception {
        instrumentation.addTransformer(new MyTransformer(),true);
        Class<?>[] loadedClassList = instrumentation.getAllLoadedClasses();
        int length = loadedClassList.length;
        for (int i = 0; i < length; ++i) {
            Class<?> clazz = loadedClassList[i];
            if (clazz.getName().replace("/", ".").equals(targetClassName)) {
                System.out.println(String.format("[Agent] Find Loaded %s %s Method!", targetClassName, targetMethodName));
                try {
                    instrumentation.retransformClasses(new Class[]{clazz});
                } catch (Throwable throwable) {
                    System.out.println("[Agent] failed to retransform class " + clazz.getName() + ": " + throwable.getMessage());
                }
                return;
            }
        }
    }
    public static String getJarFileByClass(Class cs) {
        String fileString = null;
        if (cs != null) {
            String tmpString = cs.getProtectionDomain().getCodeSource().getLocation().getFile();
            if (tmpString.endsWith(".jar")) {
                try {
                    fileString = URLDecoder.decode(tmpString, "utf-8");
                } catch (UnsupportedEncodingException var4) {
                    fileString = URLDecoder.decode(tmpString);
                }
            }
        }
        return (new File(fileString)).toString();
    }
}