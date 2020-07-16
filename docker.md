# Docker

## 基础命令

进入容器

docker exec -it mysql /bin/bash

杀死容器

docker rm -f id

查看日志

docker logs -f id

开始/通知/重启

docker start/stop/restart id

启动自动执行

docker update redis --restart=always

## mysql

### 下载与运行

docker pull mysql:5.7

```shell
docker run -p 3306:3306 --name mysql \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7

```

### 制作my.cnf

```shell
[client]
default-character-set=utf8

[mysql]
default-character-set=utf8

[mysgld]
init_connect='SET collation_ connection = utf8_ unicode_ cil'
init_connect='SET NAMES utf8'
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve

```

### 命令解释

-v 目录挂载

挂载：在Linux下访问虚拟机内部文件   （外部：内部）

-e 账户密码

-d 后台运行

## redis

### 安装运行

docker pull redis

先创建一下文件夹，不然会把redis.conf当成文件夹

makdir -p /mydata/redis/conf

touch /mydata/redis/conf/redis.conf

```shell
docker run -p 6379:6379 --name redis \
-v /mydata/redis/data:/data \
-v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf \
-d redis redis-server /etc/redis/redis.conf
```

进入客户端

docker exec -it reids redis-cli