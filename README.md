# 项目介绍

`javaAgent` 的示例代码和用法。以及在 `java rasp` 中的攻防利用。

# 注意

最好不要用 `jdk` 自带的 `Tools.jar`，会导致一个环境的 `jar` 包无法在另一个环境运行。最好用这里的 `GenericAgentTools.jar` 来兼容多平台运行。

此外，在使用 `attach` 模式注入 `rasp` 的时候注意 `Class.forName("sun.tools.attach.HotSpotAttachProvider")` 这行代码不能少，因为 `JVM` 在启动的时候不会加载 `AttachProvider` 。`agent` 模式是 `ok` 的。

这两个注意点我写前面几个模块还不知道，所以会有在运行时会有一些问题，比如在 `Linux` 平台下只能使用 `agent` 模式注入。但是懒得修改了，核心实现逻辑没问题和原理知道就行，最完善的代码看 `native-rasp` 模块中的实现。

# 分析文章
https://justdoittt.top/2023/11/20/rasp%E7%BB%95%E8%BF%87/

https://justdoittt.top/2024/04/21/hook%20native%E6%96%B9%E6%B3%95and%20bypass/