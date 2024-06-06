package com.just;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import javassist.*;
public class MyTransformer implements ClassFileTransformer {
    private final String targetClassName = "com.just.controller.DemoController";
    private final String targetMethodName = "demo";

    public MyTransformer() {
    }

    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        if("com/just/controller/DemoController".equals(className)){
            System.out.println("[Agent] start patch");
            try {
                ClassPool classPool = ClassPool.getDefault();
                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                CtMethod[] ctMethodList = ctClass.getMethods();
                int length = ctMethodList.length;
                CtMethod ctMethod = null;
                for (int i = 0; i < length; ++i) {
                    CtMethod method = ctMethodList[i];
                    if (method.getName().equals(targetMethodName)) {
                        ctMethod = method;
                        break;
                    }
                }
                assert ctMethod != null;
                ctMethod.insertBefore("System.out.println(\"fuck\");return \"fuck\";");
                System.out.println(String.format("[Agent] Patch %s %s Success!", targetClassName, targetMethodName));
                byte[] bytecode = ctClass.toBytecode();
                ctClass.detach();
                return bytecode;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return classfileBuffer;
        }
        return classfileBuffer;
    }  
}