## Docker基础

docker子命令的分类：

1. Docker环境信息：

   `info,version`

2. 容器生命周期管理：

   `Create,exec,kill,pause,restart,rm,run,start,stop,unpause`

3. 镜像仓库命令：

   `login,logout,pull,push,search`

4. 镜像管理：

   `build,images,import,load,rmi,save,tag,commit`

5. 容器运维操作：

   `attach,export,inspect,port,ps,rename,stats,top,wait,cp,diss`

6. 系统日志信息：

   `events,history,logs`

![image-20200611104327991](E:\homework\Markdown\docker\Docker基础.assets\image-20200611104327991.png)

### docker run命令

```
docker run [OPTIONS] IMAGE [COMMAND] 
```

docker run命令用来基于特定的镜像创建一个容器，并依据选项来控制容器

常用重要参数：

1. -c选项：用于给运行在容器中的所有进程分配CPU的shares值，这是一个相对权重，实际的处理速度还与宿主机的CPU有关
2. -m选项：用于限制容器中所有进程分配的内存总量
3. -v：用于挂载一个volume，可以用多个-v参数挂载多个volume。volume的格式为：[host-dir]:[container-dir]:[rw|ro]
4. -p：用于将容器的端口暴露给宿主机的端口，其常用的合适为hostPort:containerPort。通过端口的暴露，可以让外部主机通过宿主机暴露的端口来访问容器内的应用
5. -d：将容器运行于后台

### docker start/stop/restart

docker run命令可以新建一个容器来运行，对于已经存在的容器，可以通过docker start/stop/restart命令来启动、停止和重启。

docker run出来的每个新容器都有唯一的ID作为标识，而start、stop、restart一般利用容器ID标识来确定具体容器。

### docker registry

docker registry是存储容器镜像的仓库，以此来完成镜像的搜索、下载和上传等相关操作。

### docker pull

用于从docker registry中拉取image或repository。

```
docker pull [OPTIONS] name[:TAG]
```

### 镜像管理：

### docker images命令：

通过docker images命令可以列出主机上的镜像，默认列出最顶层的镜像

### docker rmi和docker rm命令：

``` 
docker rm [OPTIONS] CONTAINER [CONTAINER...]//用于删除容器
docker rmi [OPTIONS] IMAGE [IMAGE...]//用于删除镜像
```

注意在删除镜像时，如果有容器运行则无法删除镜像，此外两个命令都带-f选项，可强制删除

## 容器的运维操作

### docker attach命令

docker attach 命令可以连接到正在运行的容器，观察该容器的运行状况，或与容器的主进程直接交互

```
docker attach [OPEIONS] CONTAINER
```

### docker inspect命令

可以查看镜像和容器的详细信息，默认会列出全部信息，可以通过--format参数来指定输出的模板格式

```
docker inspect [OPTIONS] CONTAINER|IMAGE 
```

### docker ps命令

可以查看容器的相关信息，默认只显示正在运行的容器的信息

```
docker ps [OPTIONS]
```

### 其他的子命令

###  docker commit命令：

将一个容器固化为镜像

### events、history、logs命令

用于查看docker的系统日志信息