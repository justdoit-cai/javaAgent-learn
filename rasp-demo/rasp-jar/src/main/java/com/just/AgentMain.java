package com.just;

import java.lang.instrument.Instrumentation;

public class AgentMain {
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Exception {
        RaspTransformer raspTransformer = new RaspTransformer(instrumentation);
        instrumentation.addTransformer(raspTransformer,true);
        raspTransformer.retransform();
    }
}
