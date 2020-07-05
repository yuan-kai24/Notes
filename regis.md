# Redis

分布式的内存数据库。

## 安装

下载 -> 解压 -> make -> 复制

## 注意事项

1，运行的时候加上conf配置 redis-server  xxx.conf  要不然会有很多问题，如，加载不到存在文件的key，不能远程连接等。

## 配置

### 基本可远程配置

后台运行（守护进程）：daemonize no

绑定本机地址：bind 127.0.0.1

开启保护模式：protected-mode yes

改为：

daemonize yes 设置为yes后，可以通过pidfile指定pid写入的文件路径

bind 0.0.0.0 或者禁用

protected-mode no  

### 核心配置

```shell
# 关闭空闲连接时间  0代表不关闭 
timout
# 生命检测  建议设置为60
tcp-keeplive
# 日志等级 
'''
# debug (a lot of information, useful for development/testing)
# verbose (many rarely useful info, but not a mess like the debug level)
# notice (moderately verbose, what you want in production probably)
# warning (only very important / critical messages are logged)
'''
loglevel
# 日志名称
logfile
# 默认有多少库
databases
# 数据库存放目录
dir
# 缓存策略，LRU与TTL均为估算值
'''
 # volatile-lru -> 使用LRU算法移除key，只对设置了过期时间的键
 # allkeys-lru -> 使用LRU算法移除key
 # volatile-lfu -> 使用LFU算法移除key，只对设置了过期时间的键
 # allkeys-lfu -> 使用LFU算法移除key
 # volatile-random -> 在过期集合中移除随机的key，只对设置了过期时间的键
 # allkeys-random -> 移除随机的key
 # volatile-ttl -> 移除那些TTL最小的key，即那些即将过期的key
 # noeviction -> 不进行移除，针对写操作，只返回错误信息
 # 补充：
 # LRU：最近最少使用算法
 # LFU：最不经常使用算法
 # FIFO： 先进先出算法
 # MRU：最近最常使用算法
 # ARC：自适应缓存替换算法
'''
maxmemory-policy
# 设置样本数量，默认检查多少key，配合上面缓存策略
maxmemory-samples
# -----------------------------------------------------RDB begin
# 持久化时间  多少秒内改变多少次就写入磁盘
'''
# 默认
# 一分钟改了一万次
# 五分钟改了10次
# 十五分钟改了1次
'''
save 秒数 次数
# 读取备份文件路径
dbfilename
# yes：数据一致性，备份出错，停止写入，no：忽略
stop-writes-on-bgsave-error
# 是否在写入磁盘时进行压缩,不想消耗CPU就设置为no
rdbcompression
# 存储快照后的校验，使用的是CRC64算法,设置成yes后会增加大约10%的性能损耗
rdbchecksum
# -----------------------------------------------------RDB end
# -----------------------------------------------------AOF begin
# 是否开启AOF备份  默认关闭
appendonly
# 备份文件名
appendfilename
# 持久化策略
'''
# always: 同步持久化，每次变更都记录，性能差，数据完整性比较好
# everysec: 默认推荐，异步操作，每秒记录，可能丢失一秒的数据
# no：不启用
'''
appendfsync
# 重写时是否可以运用appendfsync，默认no即可，保证数据安全性
no-appendfsync-rewrite
# 设置重写时基准值
auto-aof-rewrite-min-size
# 设置重写时基准值
auto-aof-rewrite-percentage
# -----------------------------------------------------AOF end
```



## 持久化

可以通过修改save，设置持久化时间。

### RDB

Redis DataBase

在指定的事件间隔内，将内存中的数据集快照写入磁盘，也就是行话讲的Snapshot快照，恢复时是将快照文件直接读取到内存里。

单独创建（fork）一个子进程来进行持久化，先将数据写入到一个临时文件中，待持久化过程结束，再用这个临时文件替换上次持久化好的文件。

整个过程中，主进程不进行IO操作，这就确保了极高的性能，如果需要进行大规模数据的恢复，且对于数据恢复的完整性不是非常敏感，那么RDB方式要比AOF方式更加高效。

RDB的缺点是最后一次持久化后的数据可能丢失，fork时会产生两倍的膨胀性能。

保存的文件是dump.rdb文件。

#### fork

复制一个与当前进程一样的进程，新进程所有数据都和原进程一致，作为原进程的子进程。

redis-cli config set save "" 可以动态停止备份

#### 恢复

替换掉原来的dump.rdb，先备份再替换。

如果出问题，可以通过redis-check-dump进行修复。

### AOF

Append Only File

记录每一行命令，恢复的时候顺序执行命令，可以通过vim查看命令

如果打开appendonly，启动时会先读取appendonly.aof，其次才是dump.rdb

默认保存的是appendonly.aof

体积大于RDB，速度慢于RDB

#### 修复

如果appendonly出问题了，可以通过redis-check-aof进行修复

redis-check-aof --fix aof备份文件

#### rewrite

由于AOF会越来越大，所以增加了重写机制，当文件达到某个阈值时。就会对AOF文件进行压缩，只保留可以恢复数据的最小指令集，可以使用命令bgrewriteaof。

重写时也会fork一个新的子进程来重写，重写时是读取进程中数据，没有读取旧的aof中的数据，和快照类似。

redis会记录上一次重写时的aof大小，默认配置是当aof文件大小是上次rewrite后大小的一倍且文件大于64M（默认 ）时触发。

## 命令

系统操作

```shell
# 测试
ping
# 路径
config get dir
# 密码
config get requirepass
config set requirepass "密码"
# 登录
auth 密码
# 关机
shutdown
# 退出
exit
# 备份，停止写入，只管备份
save
# 异步备份
bgsave
```



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

### Set

不允许重复

入队方式存储

```shell
# 初始化或添加set/查询set/判断元素是否存在集合中
sadd/smembers/sismember
# 获取集合里面的元素个数
scard
# 删除元素
srem key val
# 随机出几个元素，默认一个
srandmember
# 随机出栈，默认一个
spop
# 移动k1中的某个值到k2
smove k1 k2 val
# 数学集合类
sdiff  # 差集
sinter # 交集
sunion # 并集
```

### Hash

KV模式不变，但V也是一个KV

```shell
# 设置/获取/多值设置/多值获取/获取全部/删除
hset/hget/hmset/hmget/hgetall/hdel
# 元素个数
hlen
# 判断键内某个键是否存在
hexists
# 某key内所有key/所有values
hkeys/hvals
# 给某key加上一个整数/浮点数   值必须为数字
hincrb/hincrbyfloat
# 若key不存在，则添加，存在则不做操作
hsetnx
```

### Zset

在set的基础上做了增强，加了一个score值，键值对模式

set是k1 v1 v2 v3

zset则是 k1 score1 v1 score2 v2

score必须为数字

允许不同的score下有相同的value

```shell
# 初始化或添加/查询区间
zadd/zrange
zrange key begin end [withsccores(带score输出)]
# 查询score范围内值，可以加limit，在结果值内截取
zrangebyscore
# 删除key下对应的value的元素
zrem
# 统计有多少个值（score与value是一个）/统计区域内个数/通过score下的值获得下标/通过score下的值查询score
zcard/zcount/zrank/zscore
# 逆序获得下标值
zrevrank
# 获得某个区间元素下标值
zrevrange
# 获得某个score范围下的值 逆序 end begin
zrevrangebyscore
```

## 事务

本质是一组命令的集合，一个事务中的所有的命令都会序列化，按顺序地串行化执行而不会被其它命令插入，不许加塞。

一个队列中，一次性，顺序性，排他性的执行一系列命令。

redis对事务是部分支持，所以没有回滚，不保证原子性。

### 使用

multi：开启事务

开启后后续操作就变成了入队。

exec：提交事务，命令出错时，全部都不能执行；命令无错，执行出错时，不会影响到其他正确的命令。

discard:放弃事务

### 监控

watch监控

悲观锁/乐观锁/CAS（check and set）

一旦执行了exec，之前加的监控锁（watch）都会被取消。

#### watch

监视一个或者多个key，如果事务执行之前这个（或这些）key被变动，那么事务将被打断。

#### unwatch

取消watch对所有key的监控。

## 消息订阅和发布

### 订阅

subscribe

可以一次性订阅多个。

支持通配符

### 发布

publish 订阅号 消息