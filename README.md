# groovy-loader
load groovy scripts in file directory dynamically

## 简介
动态加载指定目录下的groovy脚本，并将其注册为groovy bean，放置于ApplicationContext容器中，并使用命名空间进行分类区分(一个namespace对应于一个ApplicationContext)。同时能够动态感知到groovy脚本的新增、修改以及删除事件，并自动重新加载。

## 原理
- 使用spring配置文件来管理注册groovy bean：每一个spring配置文件作为一个ApplicationContext，管理一个namespace下的groovy bean
- spring配置文件使用标签<lang:groovy>，通过指定script-source来加载指定路径下的groovy脚本，通过refresh-check-delay属性来定时动态加载每个groovy bean
- 通过扫描监听指定路径下spring配置文件的变更，来接受groovy脚本的新增、删除事件

## 特性
- 能动态感知到groovy脚本的新增、删除、修改
- 针对加载得到的groovy bean，提供命名空间（根据namespace，放置到不同的ApplicationContext中）
- 用户可自定义listener，监听groovy脚本的变更
- 用户可自定义trigger，用于触发groovy bean的reload

## 使用
```
<bean id="listener" class="org.cuner.groovy.loader.test.TestListener"/><!--需要实现org.cuner.groovy.loader.listener.GroovyRefreshedListener -->
<bean id="groovyLoader" class="org.cuner.groovy.loader.NamespacedGroovyLoader">
    <property name="groovyResourcesDir" value=""/><!--指定spring groovy配置文件目录，若不设置或者为空则默认为classpath下/spring/groovy目录-->
    <property name="listener" ref="listener"/>
    <property name="trigger" ref="trigger"/>
</bean>
```