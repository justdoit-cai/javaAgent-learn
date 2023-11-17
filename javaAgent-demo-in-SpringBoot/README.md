# 代码介绍

示例使用 `javaAgent` 动态修改 `SpringBoot` 项目中的类。但是这里不知道为什么在这种情况下 `agent.jar` 需要使用插件指定添加所有的项目依赖到 `jar` 包中才可以运行成功。不然会报错找不到 `javassist` 。虽然这个报错可以理解，因为本身 `maven` 打包的时候是不会添加项目依赖的，也就自然找不到 `javassist` 。但是不知道为什么在 `javaAgent-demo` 项目中的示例代码可以不需要指定添加项目依赖也可以运行成功。

这里的 `attach-agent-jar-with-dependencies.jar` 是 `attach-agent` 项目中的工具 `jar` 包，用来一键注入 `agent` 到项目中。

```
java -jar attach-agent-jar-with-dependencies.jar springboot-javassist-bug-0.0.1-SNAPSHOT.jar com.just.SpringbootJavassistBugApplication springboot-agent-jar-with-dependencies.jar
```

```xml
 <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <!--   跳过单元测试     -->
    <maven.test.skip>true</maven.test.skip>
    <!-- 自定义MANIFEST.MF -->
    <maven.configuration.manifestFile>src/main/resources/MANIFEST.MF</maven.configuration.manifestFile>
  </properties>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.javassist</groupId>
      <artifactId>javassist</artifactId>
      <version>3.20.0-GA</version>
    </dependency>
    <dependency>
      <groupId>com.sun</groupId>
      <artifactId>tools</artifactId>
      <version>1.8.0</version>
    </dependency>
  </dependencies>
  <build>
    <finalName>springboot-agent</finalName>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-assembly-plugin</artifactId>
          <version>2.4.1</version>
          <configuration>
            <!-- get all project dependencies -->
            <descriptorRefs>
              <descriptorRef>jar-with-dependencies</descriptorRef>
            </descriptorRefs>
            <archive>
              <manifestFile>${maven.configuration.manifestFile}</manifestFile>
            </archive>
          </configuration>
          <executions>
            <execution>
              <id>make-assembly</id>
              <!-- bind to the packaging phase -->
              <phase>package</phase>
              <goals>
                <goal>single</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
  </build>
```