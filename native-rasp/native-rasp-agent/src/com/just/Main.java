package com.just;

import com.sun.tools.attach.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;
import java.util.List;

public class Main {
    // java -jar attach-agent.jar <process pid>
    public static void main(String[] args) throws AgentLoadException, IOException, AgentInitializationException, AttachNotSupportedException, ClassNotFoundException {
        if (args.length != 1) {
            System.out.println("命令格式: java -jar attach-agent.jar <process pid>");
            return;
        }
        // 这个很重要，不然会报错：com.sun.tools.attach.AttachNotSupportedException: no providers installed
        Class.forName("sun.tools.attach.HotSpotAttachProvider");
        System.out.println("[Native RASP] start inject pid " + args[0]);
        VirtualMachine virtualMachine = VirtualMachine.attach(args[0]);
        System.out.println("[Native RASP] " + args[0] + " inject success");
        virtualMachine.loadAgent(getJarFileByClass(Main.class), "xxx");
        virtualMachine.detach();
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        System.out.println("[Native RASP] =====agentmain=====");
        RaspTransformer raspTransformer = new RaspTransformer();
        inst.addTransformer(raspTransformer, true);
        if (inst.isNativeMethodPrefixSupported()) {
            String prefix = "Wrapping_";
            System.out.println("[Native RASP] add prefix Wrapping_");
            inst.setNativeMethodPrefix(raspTransformer, prefix);
        } else {
            System.out.println("[Native RASP] not support hook native method");
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        System.out.println("[Native RASP] =====premain=====");
        RaspTransformer raspTransformer = new RaspTransformer();
        inst.addTransformer(raspTransformer, true);
        if (inst.isNativeMethodPrefixSupported()) {
            String prefix = "Wrapping_";
            System.out.println("[Native RASP] add prefix Wrapping_");
            inst.setNativeMethodPrefix(raspTransformer, prefix);
        } else {
            System.out.println("[Native RASP] not support hook native method");
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