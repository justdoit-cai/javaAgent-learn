package com.just;
  
import java.lang.instrument.ClassFileTransformer;  
import java.lang.instrument.IllegalClassFormatException;  
import java.security.ProtectionDomain;  
  
public class MyTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className,  
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,  
                            byte[] classfileBuffer) throws IllegalClassFormatException {  
  
        System.out.println("=====transform=====");  
        System.out.println("className = " + className);  
        // 将常用的类名转换为 JVM 认识的类名  
        className = className.replace("/", ".");  
  
        // 如果类名为我们指定的类  
        if (className.equals("com.just.MyClass")) {
            // 进一步进行处理，替换掉输出字符串  
            return ClassHandler.replaceBytes(className, classfileBuffer);  
        }  
        return classfileBuffer;  
    }  
}