# Redis

## 安装



## 注意事项

1，运行的时候加上conf配置 redis-server  xxx.conf  要不然会有很多问题，如，加载不到存在文件的key，不能远程连接等。

## 配置

### 基本可远程配置

绑定本机地址：bind 127.0.0.1

开启保护模式：protected-mode yes

改为：

bind 0.0.0.0 或者禁用

protected-mode no  

