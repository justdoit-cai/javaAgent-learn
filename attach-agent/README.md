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

但是也不是完全用不了，只是要能够根据当前的运行平台切换 `AttachProvider` 就可以了。但是每个平台的 `tools.jar` 都只有自己的 `AttachProvider` ，我们是 `windows` 环境打包在 `Linux` 下用就会有点麻烦。但是我后来在别人的项目中发现了 `GenericAgentTools.jar` 这个 `jar` 包有多个平台环境的 `AttachProvider` ，这样就可以多平台运行了。不过这个模块我懒得改了，具体看 `native-rasp` 那个模块的实现。