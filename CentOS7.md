# 服务器（CentOS7）

记录各种服务器搭建和注意事项。

## 常用

```shell
# 打开目录
cd 
# 创建文件夹
mkdir
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
```



## nohup使用

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
firewall-cmd --zone=public --add-port=80/tcp --permanent  
# 关闭端口
firewall-cmd --remove-port=80/tcp --permanent
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
# 查看端口和pid
netstat -lnp
# 杀死端口
```

## 转码

有些时候在Windows或者其他地方编写的配置，拿到centos里不能直接使用，就需要转码

```shell
# 如果没有转码软件，可安装这款
yum install -y dos2unix
# 开始转码
dos2unix /etc/init.d/nginx
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
