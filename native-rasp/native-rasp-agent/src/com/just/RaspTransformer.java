package com.just;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class RaspTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        if("java/lang/UNIXProcess".equals(className)){
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz =null;
            try {
                System.out.println("[Native RASP] start convert java.lang.UNIXProcess");
                clazz = pool.getCtClass("java.lang.UNIXProcess");
                CtMethod method = CtNewMethod.make("int Wrapping_forkAndExec(int var1, byte[] var2, byte[] var3, byte[] var4, int var5, byte[] var6, int var7, byte[] var8, int[] var9, boolean var10);",clazz);
                method.setModifiers(Modifier.PRIVATE|Modifier.NATIVE);
                System.out.println("[Native RASP] add new native method Wrapping_forkAndExec");
                clazz.addMethod(method);
                CtMethod method1 = clazz.getDeclaredMethod("forkAndExec");
                System.out.println("[Native RASP] remove old native method forkAndExec");
                clazz.removeMethod(method1);
                CtMethod method2 = CtNewMethod.make("private int forkAndExec(int var1, byte[] var2, byte[] var3, byte[] var4, int var5, byte[] var6, int var7, byte[] var8, int[] var9, boolean var10) throws java.io.IOException { System.out.println(\"[Native RASP] exec : \"+new java.lang.String(var3)+\" \"+new java.lang.String(var4)); return this.Wrapping_forkAndExec(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);}",clazz);
                System.out.println("[Native RASP] add new method forkAndExec");
                clazz.addMethod(method2);
                byte[] bytes = clazz.toBytecode();
                return bytes;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}