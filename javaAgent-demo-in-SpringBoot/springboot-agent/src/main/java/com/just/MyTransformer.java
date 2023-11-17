package com.just;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class MyTransformer implements ClassFileTransformer {
    private Instrumentation inst;
    private final String targetClassName = "com.just.controller.TestController";
    private final String targetMethodName = "say";

    public MyTransformer(Instrumentation inst) {
        this.inst = inst;
    }

    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        className = className.replace("/", ".");
        if (className.equals(targetClassName)) {
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
        } else {
            return classfileBuffer;
        }
    }

    public void retransform() {
        Class<?>[] loadedClassList = this.inst.getAllLoadedClasses();
        int length = loadedClassList.length;
        for (int i = 0; i < length; ++i) {
            Class<?> clazz = loadedClassList[i];
            if (clazz.getName().replace("/", ".").equals(targetClassName)) {
                System.out.println(String.format("[Agent] Find Loaded %s %s Method!", targetClassName, targetMethodName));
                try {
                    this.inst.retransformClasses(new Class[]{clazz});
                } catch (Throwable throwable) {
                    System.out.println("[Agent] failed to retransform class " + clazz.getName() + ": " + throwable.getMessage());
                }
                return;
            }
        }
    }
}
