# 代码介绍

封装好了的注入 `Agent` 的工具 `jar` 包。
用法是：

```
java -jar attach-agent.jar <process name suffix> <process main class> <agent inject jar>
如：
java -jar attach-agent-jar-with-dependencies.jar MyClass com.just.MyClass D:\Project\IdeaProject\javaAgent-demo\agent-jar\target\agent-jar-1.0-SNAPSHOT.jar
```
