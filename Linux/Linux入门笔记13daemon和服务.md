# daemon和服务service

## daemon

在过去的旧版本中，常常会使用System V开机启动服务的流程

而在新的版本distribution中，改用systemd这个启动服务管理机制

好处如下：

1. 平行处理所有服务，加速开机流程
2. 在过去的system V中需要其他指令来支持，比如init，chkconfig,service等，而systemd全部仅有一只systemd服务搭配systemctl指令来处理，无需其他额外的指令。且systemd由于常驻于内存，因此任何要求(on-demand)都可以处理后续的daemon启动的任务
3. 自动服务依赖性检查
4. 根据daemon功能分类。定义所有的服务为一个服务单位（unit），systemd将服务单位区分为不同的类型(type)
5. 将多个daemons集合为一个群组
6. 向下兼容旧有的init服务

### 常见的systemd的服务类型

```
扩展名					主要服务功能
.service				一般服务类型，主要为系统服务，包括服务器本身所需要的本地服务以及网络服务
.socket					内部程序数据交换的插槽服务(socket unit)。
.target					执行环境类型（target unit）；其实是一群unit的集合
.mount/.automount		文件系统挂载相关服务
.path					侦测特点文件或目录类型
.timer					循环执行的服务
```





### 通过systemctl管理服务

```
systemctl [command] [unit]
command包含以下命令：
start	：立刻启动后面的unit
stop	：立刻停止后面的unit
restart	：立刻重启后面的unit
reload	：不关闭unit的情况下，重载配置文件，让设定生效
enable	：设定下次开机时，该unit会被启动
disable	：设定下次开机时，该unit不会被启动
status	：列出该unit的状态
is-active	：目前有没有正在运作中
is-enable	：开机时有没有预设启用这个unit
mask	：注销服务，前提是先关闭该服务
```

使用systemctl status列出unit状态：

```
● atd.service - Job spooling tools
   Loaded: loaded (/usr/lib/systemd/system/atd.service; enabled; vendor preset: enabled)
   Active: active (running) since Tue 2020-06-09 20:35:54 CST; 20h ago
 Main PID: 920 (atd)
   CGroup: /system.slice/atd.service
           └─920 /usr/sbin/atd -f
```

其中：

1. Loaded：表示开机的时候这个unit会不会启动
   1. enabled：为开机启动
   2. disabled：开机不会启动
   3. static：该daemon不能自己启动，可以被其他的enabled的服务来唤醒
   4. mask：这个daemon无论如何都无法被启动，因为已经被强制注销。可通过systemctl unmask方式改回原来的状态
2. Active：表示现在是running还是dead，除了running和dead还有:
   1. active(exited)：仅执行一次就正常结束的服务
   2. active(waiting)：被挂起，需要等待其他事件才能继续处理
   3. inactive：这个服务目前没有运作的意思



### 观察系统上所有的服务

```
systemctl [command] [--type=TYPE] [--all]
command:
	list-units		：依据unit列出目前有启动的unit。
	list-units -all	：列出所有的unit包括没启动的
	list-unit-files	：依据/usr/lib/systemd/system/内的文件，将所有的文件列表说明
	--type=TYPE ：就是之前提到的unit type,主要有service，socket，target等
```

### 透过systemctl管理不同的操作环境（target unit ）

```
systemctl list-units --type=target -all
参数：
	get-default：取得目前的target
	set-default：设定后面接的target成为默认的操作模式
	isolate	   ：切换到后面的模式
```

### 透过systemctl分析各服务之间的依赖

```
systemctl list-dependencies [unit] [--reverse]
```

### systemctl配置文件的设定项目简介

```
[root@iZi4sjttlk7jubZ ~]# cat /usr/lib/systemd/system/sshd.service 
[Unit]
Description=OpenSSH server daemon
Documentation=man:sshd(8) man:sshd_config(5)
After=network.target sshd-keygen.service
Wants=sshd-keygen.service

[Service]
Type=notify
EnvironmentFile=/etc/sysconfig/sshd
ExecStart=/usr/sbin/sshd -D $OPTIONS
ExecReload=/bin/kill -HUP $MAINPID
KillMode=process
Restart=on-failure
RestartSec=42s

[Install]
WantedBy=multi-user.target
```

参数：

1. [Unit]：unit本身的说明，以及与其他相依daemon的设定，包括在什么服务之后才启动此unit之类的设定值
2. [Service],[Socket],[Timer],[Mount],[Path]：不同的unit type会使用相应的设定项目。
3. [Install]：这个项目就是将次unit安装到哪个target里面去的意思



每个部分在存在一些细设定

1. Unit部分
   1. Description：即当我们使用systemctl list-units时，会输出给管理员看的简易说明
   2. Documentation：提供管理员能进一步的文件查询的功能，提供的大多数是手册文档之类的
   3. After：说明此unit是在哪个daemon启动之后才启动的意思，基本上仅是说明服务启动的顺序而已，而非强制的硬性要求
   4. Before：表示在服务启动之前最好启动这个服务，为规范服务启动的顺序，并非强制要求的意思
   5. Requires：明确定义此unit需要在哪个daemon启动后才能启动，此为硬性要求
   6. Wants：表示此unit之后最好还要启动什么服务比较好
   7. Conflicts：表示冲突，若该unit已启动，则某服务不能启动，若某服务已启动，则该服务不能启动
2. Service部分

