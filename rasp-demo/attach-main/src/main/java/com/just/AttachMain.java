package com.just;

import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.List;

public class AttachMain {
    public static void main(String[] args) throws AgentLoadException, IOException, AgentInitializationException, AttachNotSupportedException {
        // 获取正在运行 JVM 列表
        List<VirtualMachineDescriptor> list = VirtualMachine.list();

        // 遍历列表
        for (VirtualMachineDescriptor descriptor : list) {

            // 根据进程名字获取进程ID, 并使用 loadAgent 注入进程
            if (descriptor.displayName().endsWith("JacksonVulEnvironmentApplication")) {
                System.out.println("JacksonVulEnvironmentApplication注入成功");
                System.out.println(descriptor.id());
                VirtualMachine virtualMachine = VirtualMachine.attach(descriptor.id());
                virtualMachine.loadAgent("D:\\Project\\IdeaProject\\rasp-demo\\rasp-jar\\target\\rasp-demo.jar", "com.just.JacksonVulEnvironmentApplication");
                virtualMachine.detach();
            }
        }
    }
}
