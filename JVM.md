# JVM

java virtual machine

在虚拟机层面隐藏了底层技术的复杂性和操作系统的差异性

不要求语言

内存管理

java version "1.8.0_191"
	Java(TM) SE Runtime Environment (build 1.8.0_191-b12)
	Java HotSpot(TM) 64-Bit Server VM (build 25.191-b12, mixed mode)



HotSpot：sun公司提供的虚拟机

## 类加载

加载与连接是在程序运行时执行的---动态加载---速度换灵活---动态扩展

初始化对象：

new 关键字，getstatic,putstatic，或者调用静态方法

反射调用(java.lang.reflect)

父类没有初始化，会优先初始化父类

启动虚拟机时，优先初始化指定执行主类

java7的Method





通过数组创建对象不会初始化

调用常量（final）时，是从预先进入常量池中的字段掉出，不会触发初始化

## 运行时的内存管理



## 虚拟机的优化