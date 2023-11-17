package com.just;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class RaspTransformer implements ClassFileTransformer {
    private Instrumentation inst;
    private static String targetClassName = "java.lang.ProcessImpl";
    private static String targetMethodName = "start";

    public RaspTransformer(Instrumentation inst) {
        this.inst = inst;
    }

    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        className = className.replace("/", ".");
        if (className.equals(targetClassName)) {
            System.out.println("[RASP] start patch dangerous method");
            try {
                ClassPool classPool = ClassPool.getDefault();
                classPool.appendSystemPath();
                if (loader != null) {
                    classPool.appendClassPath(new LoaderClassPath(loader));
                }
                CtClass ctClass = null;
                ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
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

                ctMethod.insertBefore("System.out.println(\"[RASP] java.lang.ProcessImpl.start() is forbidden!\");return null;");
                System.out.println(String.format("[RASP] Patch %s %s Success!", targetClassName, targetMethodName));
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

    /**
     * TODO:
     * 初始情况java.lang.ProcessImpl类是没有被JVM加载的，只有被RCE过之后才会被加载，从而下面我们才能获取到，我们不可能让黑客黑了一次再做1防范。
     * 因此我们需要考虑如何一开始就修改这个类。
     */
    public void retransform() {
        Class<?>[] loadedClassList = this.inst.getAllLoadedClasses();
        int length = loadedClassList.length;
        for (int i = 0; i < length; ++i) {
            Class<?> clazz = loadedClassList[i];
            if (clazz.getName().replace("/", ".").equals(targetClassName)) {
                System.out.println(String.format("[RASP] Find Loaded %s %s Method!", targetClassName, targetMethodName));
                try {
                    this.inst.retransformClasses(new Class[]{clazz});
                } catch (Throwable throwable) {
                    System.out.println("[RASP] failed to retransform class " + clazz.getName() + ": " + throwable.getMessage());
                }
                return;
            }
        }
    }
}