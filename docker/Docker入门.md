# 容器数据卷

## 容器数据卷的背景

在docker中容器中包含着应用的数据，当我们删除容器时，数据也会被删除，故有容器数据卷来帮助，将数据存储在本地。

本质上就是挂载，将容器中的目录挂载到Linux上。

容器持久化和同步操作，容器间数据共享

## 容器卷使用

```shell
# 方式一：使用命令来挂载
 docker run -it -v /home/dockertest:/home centos /bin/bash
 
 -v destination(主机):source（容器）
```

![image-20200611151336021](E:\homework\Markdown\docker\Docker入门.assets\image-20200611151336021.png)

### 具名和匿名挂载

三种挂载方式：

```shell
-v 容器内路径 		 # 匿名挂载
-v 卷名：容器内路径  	#具名挂载
-v /宿主路径：容器内路径 #指定路径挂载
docker build -v /home/lu/log:/log/ -p 8085:8085 -t lu_blog:1.1
```

给容器内的路径增加读写权限：

```
ro readonly 只读
rw readwrite 读和写

docker run -d -P -name contName -v juming-ngxin:/etc/nginx:ro nginx
docker run -d -P -name contName -v juming-ngxin:/etc/nginx:rw nginx
```

### 初试dockerfile

dockerfile是用来创建docker镜像的文件（本质是命令脚本

在脚本中可以使用匿名卷挂载（之后常用

```shell
docker build [-f dockerfile位置] [-t 镜像名以及tag]
```

### 数据卷容器

多个容器的同步

```
关键命令：--volums-from dockername
创建一个容器，将该容器作为dockername的子容器，因此他们的挂载文件夹是共享的
注意：当有两个容器挂载到该文件夹时，只要有一个容器存在，则该挂载文件夹不会失效，持续到没有容器为止
```

### dockerfile构建过程

基础知识：

1. 每个关键字指令都为大写字母
2. 执行从上到下
3. #表示注释
4. 没一个指令都为一个镜像层

### docker 的指令

```shell
FROM 			## 基础镜像
MAINTAINER		## 姓名加联系方式
RUN				## 镜像构建时需要的命令
ADD				## 步骤：例如，Tomcat镜像，添加的内容
WORKDIR			## 镜像的工作目录
VOLUME			## 挂载的目录
EXPORT			## 暴露的端口
CMD				## 指定容器启动时要运行的命令，会替代
ENTRYPOINT		## 指定容器启动时要运行的命令，会追加
COPY 			## 类似于ADD，帮我们的文件拷贝到镜像中
ENV				## 构建时设置环境变量
```

学习dockerfile的技巧：使用docker history 镜像名



### 发布镜像到dockerHub

1. 注册登录dockerhub
2. 命令行登录
3. 登录成功，docker push

tips:可以使用阿里云 提供的镜像服务，详细的操作流程在阿里云有

## docker 网络

![image-20200612084912101](E:\homework\Markdown\docker\Docker入门.assets\image-20200612084912101.png)

当添加了一个容器之后，再次使用ip addr会发现多了一个地址。且不同的容器间可以ping通

![image-20200612090729691](E:\homework\Markdown\docker\Docker入门.assets\image-20200612090729691.png)

![image-20200612091052292](E:\homework\Markdown\docker\Docker入门.assets\image-20200612091052292.png)

注意，当删除一个容器时，其对应的容器IP也会被删除

### 使用link连接容器

```
docker run -d -P --link containName imageName
```

这样可以使：子容器ping通父容器

缺点：父容器不能ping通子容器，需要额外配置

## 自定义网络

查看所有docker网络

```
docker network ls
```

![image-20200612094349642](E:\homework\Markdown\docker\Docker入门.assets\image-20200612094349642.png)

网络模式：

1. bridge：桥接模式（默认模式
2. none：不配置网络
3. host：和宿主机共享网络
4. container：容器网络连接（不常用

自定义命令：

```
 docker network create  --driver bridge --subnet 192.168.0.0/16 --gateway 192.168.0.1 mynet
```

![image-20200612094443375](E:\homework\Markdown\docker\Docker入门.assets\image-20200612094443375.png)

### 将容器与自定义网络绑定

命令：

```shell
docker run -d -P --name ContainName --net netName  imagesName  
```

绑定完后的结果

```shell
[
    {
        "Name": "mynet",
        "Id": "c0be35552aa29c0f73ee8c804ed302310369978f8a2189239cf3429e23e4d57a",
        "Created": "2020-06-12T09:41:45.273231074+08:00",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "192.168.0.0/16",
                    "Gateway": "192.168.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "768e979ae0d1aeae1af15c60bb13dd38e2aacd1f3ef29b5f59eaf0e065821a1b": {
                "Name": "tomcat-net01",
                "EndpointID": "16b6b44d4ecc81571bf40efe34e867b25205a985b273d22e41939952a569ee13",
                "MacAddress": "02:42:c0:a8:00:02",
                "IPv4Address": "192.168.0.2/16",
                "IPv6Address": ""
            },
            "838f2c7c777ff7105acb2d0bf500b362c8f9d24941033611012e737bdfd902ac": {
                "Name": "tomcat-net02",
                "EndpointID": "35a35f477b3214fadc48a12e40614c19e0303109ee4c29ed06257130f5da7811",
                "MacAddress": "02:42:c0:a8:00:03",
                "IPv4Address": "192.168.0.3/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```

好处：

1. 容器间可以通过服务名（即容器名）就可以互相ping通，而不需要IP地址（防止IP地址变化）

![image-20200612094951855](E:\homework\Markdown\docker\Docker入门.assets\image-20200612094951855.png)

2. 不同集群使用不同的网络，保持集群的网络安全

### docker network connect

```shell
docker network connect netName ContainerName
```

![image-20200612095757645](E:\homework\Markdown\docker\Docker入门.assets\image-20200612095757645.png)

![image-20200612095813990](E:\homework\Markdown\docker\Docker入门.assets\image-20200612095813990.png)

一个容器两个地址！