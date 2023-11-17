# 代码介绍
通过 `javaAgent` 技术动态修改运行中的类的示例代码。

# 使用方式
首先运行 `running-demo` 模块中的 `MyClass` 类，然后将 `agent-jar` 模块编译为 `jar` 包，最后在 `agent-main` 模块填入前面生成好的 `jar` 包的路径，然后启动 `agent-main` 中的主类就可以成功注入了。