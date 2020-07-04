# Redis

## 安装

下载 -> 解压 -> make -> 复制

## 注意事项

1，运行的时候加上conf配置 redis-server  xxx.conf  要不然会有很多问题，如，加载不到存在文件的key，不能远程连接等。

## 配置

### 基本可远程配置

绑定本机地址：bind 127.0.0.1

开启保护模式：protected-mode yes

改为：

bind 0.0.0.0 或者禁用

protected-mode no  





## 命令

### 关键字操作

```shell
# 设置键值
set 
# 获取对应键的值
get 
# 查看键
keys  
# 判断键是否存在
existis 
# 设置键过期时间
expire 
# 选择库
select 
# 移动某个键到某个库
move 
# 清空所有库
flushall 
# 清空当前库
flushdb 
# 查看键是否过期 -1代表长期存在  -2 代表过期或不存在
ttl 
# 查看数据类型
type
# 删除
del
```

### String

```shell
# 设置/获取/删除/添加/长度
set/get/del/append/strlen
# 默认加一/默认减一/增加指定值/减少指定值 <--(需要值为数字 “01”也不行)
incr/decr/incrby/decrby
# 获取字符串key中某区间字符串/设置。。。
getrange/setrange
# 值存在时间/如果没有这个健，就创建，没有就不操作
setex 健 秒 值 / setnx
# 设置多个键值/获取多个值/设置多个setnx
mset/mget/msetnx
# 先获取再设置（获取旧值，设置新值）
getset
```

### List

list存储顺序为压栈方式

```shell
# 存储list/从右往左存储list/获取list  (获取所有【反向】： lrange key 0 -1)
lpush/rpush/lrange
# 出栈（先进后出）/出队（先进先出）
lpop/rpop
# 按照索引获得元素---栈
lindex
# 删除list中n个值为val的数据
lrem key n val
# 裁剪指定区间，其余数据被删除
ltrim key begin end
# 源列表 目的列表   （把源列表的rpop出来到目标列表的lpush）
rpoplpush
# 设置某个下标的值
lset
# 插入值
linsert key before/after val1 val2 ...
```

