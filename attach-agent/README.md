# 代码介绍

封装好了的注入 `Agent` 的工具 `jar` 包。
用法是：

```
java -jar attach-agent.jar <process name suffix> <process main class> <agent inject jar>
如：
java -jar attach-agent-jar-with-dependencies.jar MyClass com.just.MyClass D:\Project\IdeaProject\javaAgent-demo\agent-jar\target\agent-jar-1.0-SNAPSHOT.jar
```

但是发现这个代码只能在 `windows` 上运行。因为这里的 `tools.jar` 是属于 `Windows` 的 `jre` 的，在 `Linux` 平台用不了。会报错：
```
Exception in thread "main" java.lang.UnsatisfiedLinkError: sun.tools.attach.WindowsAttachProvider.tempPath()Ljava/lang/String;
```

但是也不是完全用不了，只是要能够根据当前的运行平台切换 `AttachProvider` 就可以了。但是我发现 `Windows` 的 `tools.jar` 没有 `LinuxAttachProvider`，也就无法在 `Linux` 下用了。我没有找到有 `LinuxAttachProvider` 的 `tools.jar`。因此 `Linux` 下建议如果需要动态修改类，只能在程序启动的时候指定 `agent` `jar` 包的位置，使用 `java -jar -javaagent:<agent.jar的位置> <运行的程序>。想在程序运行的时候通过 `attach` 来修改不太容易...