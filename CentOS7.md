# 服务器（CentOS7）

记录各种服务器搭建和注意事项。

## 常用

```shell
# 打开目录
cd 
# 创建文件夹(-p多个)
mkdir -p
# 删除
rm -rf
# 解压tar.gz
tar -zxvf 
# 解压tar.bz2
tar -jxvf 
# 查找ifconfig安装包
yum search ifconfig 
# weget
yum -y install wget
# 警告 The remote SSH server rejected X11 forwarding request
yum install xorg-x11-xauth -y
# 安装vim
yum -y install vim
# 重启
reboot
# 给文件夹设置权限
chmod -R 777 xxx
# 查看某服务（服务名可以使用关键字）
ps -ef|frep 服务名
```



## nohup用

用于不间断运行，需要安装coreutils

```
yum install coreutils
```
安装后如果不能直接用，就找到路径用，我的是/usr/bin/nohup

一般使用方式是nphup command &

其中&是后台运行

使用它的原因是在直接跑spring boot的jar包时，离开终端它也断开了，所以弄了个这个。

## 熵

用到这玩意是因为在使用tomcat时，启动不止慢，还卡住了，百度说是熵池太小，需要增大或者安装熵服务，至于什么是熵，留待后来研究

```shell
yum install rng-tools # 安装rngd服务（熵服务，增大熵池） 
systemctl enable rngd # 设置服务enable,启动机器就启动服务 
systemctl start rngd # 启动服务
```

## 防火墙

```shell
# 查询防火墙状态
firewall-cmd --state
# 查看开放端口
firewall-cmd --list-ports 
# 开启端口
firewall-cmd --zone=public --add-port=端口/tcp --permanent  
# 关闭端口
firewall-cmd --remove-port=端口/tcp --permanent
命令含义:
–zone #作用域
–add-port=80/tcp #添加端口，格式为：端口/通讯协议
–permanent #永久生效，没有此参数重启后失效

# 关闭防火墙
systemctl stop firewalld.service
# 开启防火墙
systemctl start firewalld.service
# 禁止firewall开机启动
systemctl disable firewalld.service
# 设置开机启动
systemctl enable firewalld.service
# 允许http通信
firewall-cmd --permanent --zone=public --add-service=http
# 允许https通信
firewall-cmd --permanent --zone=public --add-service=https
# 重启防火墙
firewall-cmd --reload
```

## 端口

```shell
# 安装
yum install net-tools
# 查看端口和pid
netstat -lnp
# 杀死端口
```

## 转码

有些时候在Windows或者其他地方编写的配置，拿到centos里不能直接使用，就需要转码

```shell
# 安装
yum install -y dos2unix
# 开始转码
dos2unix xxx
```

## FTP

```shell
# 首先要查看你是否安装vsftp
rpm -q vsftpd
vsftpd-3.0.2-10.el7.x86_64  （显示也就安装成功了！）
# 如果没有则安装vsftpd
yum install -y  vsftpd
# 完成后再检查一遍
whereis vsftpd
vsftpd:/usr/sbin/vsftpd /etc/vsftpd/usr/share/man/man8/vsftpd
# 查看vsftpd服务的状态
systemctl status vsftpd.service
(如果是激活状态的话会有active绿色的标记)
# 激活vsftpd服务
systemctl start vsftpd.service
# 设置vsftpd服务开机自启
systemctl enable vsftpd.service
# 查看配置文件路径
rpm -qc vsftpd
# 创建密码明文文件（格式为  用户名 换行 密码）可设多个用户
vim /etc/vsftpd/uftp.txt
# 根据明文创建密码DB文件
db_load -T -t hash -f /etc/vsftpd/uftp.txt /etc/vsftpd/uftp.db
# 为vsftpd添加guest账户
useradd -d /home/wwwroot -s /sbin/nologin uftp
# 修改 /etc/pam.d/vsftpd,将原本所有内容注释掉
查看版本号：getconf LONG_BIT 
系统为32位： 
auth required pam_userdb.so db=/etc/vsftpd/uftp
account required pam_userdb.so db=/etc/vsftpd/uftp
系统为64位： 
auth required /lib64/security/pam_userdb.so db=/etc/vsftpd/uftp
account required /lib64/security/pam_userdb.so db=/etc/vsftpd/uftp
# 修改配置文件
vim /etc/vsftpd/vsftpd.conf，
将# anonymous_enable=YES 改为 anonymous_enable=NO
添加如下代码：
guest_enable=YES
guest_username=uftp
allow_writeable_chroot=YES
virtual_use_local_privs=YES
# 更改密码
passwd 用户名
# 这种方式也可以，码住
echo "密码" | passwd ftpuser --stdin
# 授权
chmod o+w /usr/local/nginx/html/
# 查看selinux信息：
getsebool -a|grep ftp
# 设置selinux权限(名字已selinux为准)：
setsebool -P allow_ftpd_full_access on
setsebool -P tftp_home_dir on
# 登陆方式
ftp://用户名:密码@ip
# 命令都可以触类旁通（<-_->要不是怕以后自己看不懂，都不想写）

# 问题记录

# 227问题
# 服务器端：
ftp localhost 
# 输入用户名和密码
passive #（关闭被动模式，再输入一次即开启）

# 权限设置(自己设置时弄了777)
chmod -R 775 /home/wwwroot
# vsftpd.conf端口范围设置（前提是这些端口要开放）
pasv_min_port=30000
pasv_max_port=31000
```

## mysql

```shell
# 检查是否已安装过mariadb，若有便删除
rpm -qa | grep mariadb
rpm -e --nodeps mariadb-libs-5.5.44-2.el7.centos.x86_64
# 检查，创建用户组
cat /etc/group | grep mysql
cat /etc/passwd |grep mysql
groupadd mysql
useradd -r -g mysql mysql
# 下载解压移动
wget https://dev.mysql.com/get/Downloads/MySQL-5.7/mysql-5.7.24-linux-glibc2.12-x86_64.tar.gz
tar xzvf mysql-5.7.24-linux-glibc2.12-x86_64.tar.gz
mv mysql-5.7.24-linux-glibc2.12-x86_64 /usr/local/mysql
# 创建引用data目录
mkdir /usr/local/mysql/data
# 更改目录组合
cd /usr/local/ 
chown -R mysql:mysql mysql/
chmod -R 755 mysql/
# 安装  -----最后一行会打印密码
/usr/local/mysql/bin/mysqld --initialize --user=mysql --datadir=/usr/local/mysql/data --basedir=/usr/local/mysql
# 如果报错/usr/local/mysql/bin/mysqld:error while loading shared libraries:libaio.so.1 :cannot open shared object file:NO such file or directory缺少libaio，安装即可
yum install libaio
# 启动
/usr/local/mysql/support-files/mysql.server start
# 制作软连接并重启
ln -s /usr/local/mysql/support-files/mysql.server /etc/init.d/mysql 
service mysql restart
# 制作在usr/bin下的软连接
ln -s /usr/local/mysql/bin/mysql /usr/bin
# 修改密码，开启远程可用
mysql -u root -p
alter user 'root'@'localhost' identified by '123456';
use mysql;
update user set user.Host='%' where user.User='root';
flush privileges;
quit/exit（俩都可以）
# 编辑my.cnf
vi /usr/local/mysql/my.cnf
[mysqld]
port = 3306
sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES
# 配置init.d,开机启动
cp /usr/local/mysql/support-files/mysql.server /etc/init.d/mysqld
# 赋予可执行权限
chmod +x /etc/init.d/mysqld
# 添加服务
chkconfig --add mysqld
# 显示服务列表
chkconfig --list
# 重启
reboot
```

## Nginx

Nginx安装

基础环境

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

配置

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
# 开始转码
dos2unix /etc/init.d/nginx
```

相关命令（触类旁通版）

```shell
service nginx restart  重启nginx
chkconfig nginx on  开机启动
```

Nginx卸载

停止Nginx软件

```shell
service nginx stop
```

删除Nginx的自动启动

```shell
chkconfig nginx off
```

从源头删除Nginx

```shell
rm -rf /usr/sbin/nginx
rm -rf /etc/nginx
rm -rf /etc/init.d/nginx
```

再使用yum清理

```shell
yum remove nginx
```

配置

这些配置为用过的配置

多端口映射

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

支持WS

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



## 欢迎语

开机前的欢迎语

```shell
vim /etc/motd
```

## 开机执行的命令

```shell
# 增加rc.local可执行权限
chmod +x /etc/rc.d/rc.local
# 添加命令
vim /etc/rc.d/rc.local
```

## 持续监测日志

```shell
tail
-n # 后面接数字，代表显示几行的意思
-f # 表示持续侦测后面所接的文件名，要等到按下[ctrl]-c才会结束tail的侦测

```

## SVN

```shell
# 安装
yum install subversion
# 查看是否成功
svnserve --version
mkdir -p /var/svn
# 创建目录
mkdir -p /var/svn/mypro
# 创建仓库
svnadmin create /var/svn/mypro
```

目录说明

```properties
# hooks目录：放置hook脚步文件的目录
# locks目录：用来放置subversion的db锁文件和db_logs锁文件的目录，用来追踪存取文件库的客户端
# format目录：是一个文本文件，里边只放了一个整数，表示当前文件库配置的版本号
# conf目录：是这个仓库配置文件（仓库用户访问账户，权限）
```

配置

```shell
# conf配置
# passd配置
username=password
# -----------------------------------------------
# svnserver.conf配置
# 读写
anon-access = read
auth-access = write
# 账户配置
password-db = passwd 
# 权限配置
authz-db = authz
# 此处写仓库路径
realm = /var/svn/mypro
# -----------------------------------------------
# authz配置
# 在group下配置
# 创建username组xxx为成员
admin = u1,u2
user = u3
[/xxx]
# 读写
@admin = rw
# 只读
@user = r
# 表示除了上面设置的权限用户组以外，其他所有用户都设置空权限，空权限表示禁止访问本目录，这很重要一定要加上。
*=
```

启动与关闭

```shell
 # 启动 --listen-port=3690可以省略
 svnserve -d -r /var/svn/mypro --listen-port=3690
 # 关闭
 killall svnserve
```

客户端

```shell
 # svn://服务器地址/mypro
```

