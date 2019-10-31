# Go语言学习

## 安装

下载地址：

https://golang.google.cn/dl/

安装方式：

直接安装

## 运行

普通运行

go run xxx.go

测试运行

go test -v xxx.go

编译

go build xxx.go

## 语言特征

### 测试篇

文件以_test.go结尾

### 类型篇

```go
var [name]  [type] = 值

[name] := [值]//具备一定的类型推断能力

const [name] [type] = [值]

var (
	[name] [type] = [值]
	[name]  = [值] // 可以不加类型，自我判断
    ...

)
const(
	[name] [type] = [值]
)

// 数组
a := [...]类型{value1, value2,...}

```

不支持隐式类型转换

### 指针篇

不支持指针运算

### 运算符篇

不支持前置++和--

==时数组是比较里面的值（顺序与值都要相同）

**位运算**

&^按位清零

### 循环判断

循环只有for，没有（）

判断也没有（）

**技巧**

```go
if a := 1 == 1; a{
	fmt.Println("1 == 1", a)
}
if v,err := function(); err==nil{
    fmt.Println("无错执行方法")
}else{
    fmt.Println("有错执行方法")
}
```

switch不用加break，没有case穿透问题

## 常用函数篇

**len**

数组长度