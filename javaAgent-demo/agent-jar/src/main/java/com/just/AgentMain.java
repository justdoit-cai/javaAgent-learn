package com.just;

import java.lang.instrument.Instrumentation;

public class AgentMain {
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Exception {
        System.out.println("=====agentmain=====");
        instrumentation.addTransformer(new MyTransformer(),true);
        instrumentation.retransformClasses(Class.forName("com.just.MyClass"));
    }
}
