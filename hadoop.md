# Hadoop



## 本地模式

hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar  grep input/  output  'dfs[a-z.]+'

不能有output文件

## WordCount

统建词汇

创建一个wcinput

创建一个wc.input

hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount wcinput/ wcount

不能有count文件



## 伪分布式

etc下Hadoop文件修改



修改core-site.xml

<!-- 指定HDFS中NameNode的地址 -->

<property>

<name>fs.defaultFS</name>

  <value>hdfs://hadoop101:9000</value>

</property>

 

<!-- 指定Hadoop运行时产生文件的存储目录 -->

<property>

  <name>hadoop.tmp.dir</name>

  <value>/opt/module/hadoop-2.7.2/data/tmp</value>

</property>

修改hadoop.env.sh

修改其中javahome



修改hdfs-site.xml

<!-- 指定HDFS副本的数量 -->

<property>

  <name>dfs.replication</name>

  <value>1</value>

</property>



格式化NameNode

第一次启动格式化，以后不要总是格式化

bin/hdfs namenode -format

 hadoop namenode -format

启动守护进程

sbin/hadoop-daemon.sh start namenode

sbin/hadoop-daemon.sh start datanode



hdfs dfs -mkdir -p /user/yuankai/input

hdfs dfs -ls   /

hdfs dfs -lsr   /

hdfs dfs -put  源文件  /user/yuankai/input

hdfs dfs -rm -r /user/yuankai/output

bin/hdfs dfs -rm -r /user/yuankai/input/wcount



yarn配置

配置yarn-env.sh中的Javahome

配置yarn-site.xml

<!-- Reducer获取数据的方式 -->

<property>

   <name>yarn.nodemanager.aux-services</name>

   <value>mapreduce_shuffle</value>

</property>

 

<!-- 指定YARN的ResourceManager的地址 -->

<property>

<name>yarn.resourcemanager.hostname</name>

<value>hadoop101</value>

</property>

配置mapred-site.xml

<!-- 指定MR运行在YARN上 -->

<property>

   <name>mapreduce.framework.name</name>

   <value>yarn</value>

</property>



启动

sbin/yarn-daemon.sh start resourcemanager

sbin/yarn-daemon.sh stop resourcemanager

sbin/yarn-daemon.sh start nodemanager

sbin/yarn-daemon.sh stop nodemanager

sbin/mr-jobhistory-daemon.sh start historyserver

sbin/mr-jobhistory-daemon.sh stop historyserver

端口：8088

测试：hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /user/yuankai/input /user/yuankai/output

### 配置日志聚集

需要关闭资源服务

yarn-site.xml

<!-- 日志聚集功能使能 -->

<property>

<name>yarn.log-aggregation-enable</name>

<value>true</value>

</property>

 

<!-- 日志保留时间设置7天 -->

<property>

<name>yarn.log-aggregation.retain-seconds</name>

<value>604800</value>

</property>



在分布式的情况下，yarn只能在配置的指定节点下启动

## HDFS

块的大小设置主要取决于磁盘传输速率。