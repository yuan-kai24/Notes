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
a := [...]类型{value1, value2,...} // 自动初始化
a := [num]类型{value1 * num} // 初始化固定个数
a := [num1][num2]类型{{value1 * num1},{value * num2}}
a[n]  // 访问也是通过下标访问
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

```go
arr3 := [...]int{1,3,4,5}
for _, e := range arr3{
	t.Log(e)
}
```



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

switch，没有 （），不用加break，没有case穿透问题

```go
for i := 1; i < 6; i++ {
	switch i {
		case 1,3,5:
			t.Log("Even")
		case 2,4:
			t.Log("Odd")
    }
}
for i := 1; i < 6; i++ {
    switch {
		case i%2 == 0:
			t.Log("Even")
		case i%2 == 1:
			t.Log("Odd")
    }
}
```

### 数组截取

与Python类似，但是不支持[:-1]这种操作

a[开始索引（包含）：结束索引(不包含)]

### Map

```go
m := map[string]int{"one": 1, "two": 2,...}

m1 := map[string]int{}

m1["one"] = 1

m2 := make(map[string]int, 10/*Initial Capacity*/)
```

**判断key,value是否存在**

```go
if v, ok := m[1]; ok {
    t.Log(ok, v)
} else {
    t.Log("不存在")
}
```

**遍历**

```go
for k,v := range m{
    t.Log(k, " ", v)
}
```

**函数调用**

```go
m:=map[int]func(op int)int{}
m[1]=func(op int) int {return op}
m[2]=func(op int) int {return op*op}
t.Log(m2[1](2))
t.Log(m2[2](2))
```

### 字符串

不可变的byte slice

不能s[n] = 'x'来使用

**Unicode & UTF-8**

utf-8是Unicode的一种实现



## 常用函数篇

**len**

数组长度

## VsCode篇

```json
# 测试无日志输出时，在工作区添加
"go.testFlags": ["-v"]
```

