# Nginx

## 安装卸载

### Nginx安装

#### 	基础环境

```shell
yum install -y gcc-c++
yum install -y pcre pcre-devel
yum install -y zlib-devel
yum install -y openssl openssl-devel
```

​	将nginx-1.8.0.tar.gz文件传到root目录：

```shell
cd /root
wget http://nginx.org/download/nginx-1.8.0.tar.gz
tar -zxvf nginx-1.8.0.tar.gz
cd /root/nginx-1.8.0
```

#### 	配置

```shell
./configure \
--prefix=/usr/local/nginx \
--pid-path=/var/run/nginx/nginx.pid \
--lock-path=/var/lock/nginx.lock \
--error-log-path=/var/log/nginx/error.log \
--http-log-path=/var/log/nginx/access.log \
--with-http_gzip_static_module \
--http-client-body-temp-path=/var/temp/nginx/client \
--http-proxy-temp-path=/var/temp/nginx/proxy \
--http-fastcgi-temp-path=/var/temp/nginx/fastcgi \
--http-uwsgi-temp-path=/var/temp/nginx/uwsgi \
--http-scgi-temp-path=/var/temp/nginx/scgi
```

​	编译，安装

```shell
make && make  install
```

​	创建目录

```shell
mkdir /var/temp
mkdir /var/temp/nginx/
mkdir /var/temp/nginx/client
```

​	以上完成后，在 /usr/local/nginx/sbin目录下，有个nginx执行文件，./nginx执行即可

配置服务

​	放到/etc/init.d/nginx下即可(懒得操作可以放掉这一步)

```shell
#!/bin/bash
# nginx Startup script for the Nginx HTTP Server
# it is v.0.0.2 version.
# chkconfig: - 85 15
# description: Nginx is a high-performance web and proxy server.
#              It has a lot of features, but it's not for everyone.
# processname: nginx
# pidfile: /var/run/nginx.pid
# config: /usr/local/nginx/conf/nginx.conf
nginxd=/usr/local/nginx/sbin/nginx
nginx_config=/usr/local/nginx/conf/nginx.conf
nginx_pid=/var/run/nginx.pid
RETVAL=0
prog="nginx"
# Source function library.
. /etc/rc.d/init.d/functions
# Source networking configuration.
. /etc/sysconfig/network
# Check that networking is up.
[ ${NETWORKING} = "no" ] && exit 0
[ -x $nginxd ] || exit 0
# Start nginx daemons functions.
start() {
if [ -e $nginx_pid ];then
   echo "nginx already running...."
   exit 1
fi
   echo -n $"Starting $prog: "
   daemon $nginxd -c ${nginx_config}
   RETVAL=$?
   echo
   [ $RETVAL = 0 ] && touch /var/lock/subsys/nginx
   return $RETVAL
}
# Stop nginx daemons functions.
stop() {
        echo -n $"Stopping $prog: "
        killproc $nginxd
        RETVAL=$?
        echo
        [ $RETVAL = 0 ] && rm -f /var/lock/subsys/nginx /var/run/nginx.pid
}
# reload nginx service functions.
reload() {
    echo -n $"Reloading $prog: "
    #kill -HUP `cat ${nginx_pid}`
    killproc $nginxd -HUP
    RETVAL=$?
    echo
}
# See how we were called.
case "$1" in
start)
        start
        ;;
stop)
        stop
        ;;
reload)
        reload
        ;;
restart)
        stop
        start
        ;;
status)
        status $prog
        RETVAL=$?
        ;;
*)
        echo $"Usage: $prog {start|stop|restart|reload|status|help}"
        exit 1
esac
exit $RETVAL
```

文件写完后，可能权限不够，需要授权

```shell
chmod a+x /etc/init.d/nginx
```

如果文件不存在，则转码

```shell
# 如果没有转码软件，可安装这款
yum install -y dos2unix
# 开始转码
dos2unix /etc/init.d/nginx
```

相关命令（触类旁通版）

```shell
service nginx restart  重启nginx
chkconfig nginx on  开机启动
```

### Nginx卸载

#### 停止Nginx软件

```shell
service nginx stop
```

#### 删除Nginx的自动启动

```shell
chkconfig nginx off
```

#### 从源头删除Nginx

```shell
rm -rf /usr/sbin/nginx
rm -rf /etc/nginx
rm -rf /etc/init.d/nginx
```

#### 再使用yum清理

```shell
yum remove nginx
```

## 配置

这些配置为用过的配置

### 多端口映射

需要几个就复制几个

```shell
server{
                listen 80;
                server_name 域名;

                location /{
                        proxy_set_header X-Real-IP $remote_addr;
                        proxy_set_header Host $http_host;
                        proxy_pass http://127.0.0.1:8080;
                }
        }
      
```

### 支持WS

上面那种方式不支持WS协议

```shell
server{
                listen 80;
                server_name 域名;

                location /{
                        proxy_http_version 1.1;
                        proxy_set_header Upgrade $http_upgrade;
                        proxy_set_header Connection "upgrade";
                        proxy_set_header X-Real-IP $remote_addr;
                        proxy_set_header Host $http_host;
                        proxy_pass http://127.0.0.1:8081;
                }
        }

```

