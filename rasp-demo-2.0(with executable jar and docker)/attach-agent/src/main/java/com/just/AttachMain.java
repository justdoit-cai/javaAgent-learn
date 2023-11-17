package com.just;

import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.List;

public class AttachMain {
    // java -jar attach-agent.jar <process name suffix> <process main class> <agent inject jar>
    public static void main(String[] args) throws AgentLoadException, IOException, AgentInitializationException, AttachNotSupportedException {
        if (args.length != 3) {
            System.out.println("命令格式: java -jar attach-agent.jar <process name suffix> <process main class> <agent inject jar>");
            return;
        }
        System.out.println("start inject");
        boolean flag = false;
        // 获取正在运行 JVM 列表
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        // 遍历列表
        for (VirtualMachineDescriptor descriptor : list) {
            // 根据进程名字获取进程ID, 并使用 loadAgent 注入进程
            if (descriptor.displayName().endsWith(args[0])) {
                flag = true;
                System.out.println(args[0] + " inject success");
                System.out.println(descriptor.id());
                VirtualMachine virtualMachine = VirtualMachine.attach(descriptor.id());
                virtualMachine.loadAgent(args[2], args[1]);
                virtualMachine.detach();
            }
        }
        if (!flag) {
            System.out.println(args[0] + " inject fail. It not found!");
        }
    }
}
