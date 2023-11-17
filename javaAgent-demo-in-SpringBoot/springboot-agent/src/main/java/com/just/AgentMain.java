package com.just;

import java.lang.instrument.Instrumentation;

public class AgentMain {
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Exception {
        MyTransformer myTransformer = new MyTransformer(instrumentation);
        instrumentation.addTransformer(myTransformer,true);
        myTransformer.retransform();
    }
}
