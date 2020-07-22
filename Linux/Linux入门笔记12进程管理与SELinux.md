# 进程管理与SELinux

## 进程

触发任意一个事件时，系统都会给他定义成一个进程，而一个进程通常有一个ID，叫做PID

### 进程和程序

程序一般放在实体磁盘中，当用户调用该程序时，程序会进入内存成为一个个体，即进程。每一个程序根据调用者的UID\GID的不同来给与不同的权限。

![image-20200609111340051](.\Linux入门笔记12进程管理与SELinux.assets\image-20200609111340051.png)

并且该进程衍生出来的其他进程，也会沿用该进程的权限

列出进程的命令：ps -l

### 子进程的产生方式：fork and exec

当父进程需要衍生子进程时，首先会复制一个和父进程一模一样的子进程，之后子进程再以exec运行响应的程序

![image-20200609132940718](.\Linux入门笔记12进程管理与SELinux.assets\image-20200609132940718.png)

### 服务的概念：常驻于内存的程序



## job control

前景和背景的概念：

1. 前景：在bash中出现提示字符让进行操作的即为前景
2. 背景：除了前景，其他的一些工作将放在背景中运行，且放入背景的工作是不可能使用`ctrl+c`终止

### 将指令放在后台执行：

bash下的子进程置于后台运行：

```
cp file1 fil2 &
```

`'&'`表示将file1复制到file2，并置于背景进行

### 将指令放在后台暂停

在使用一个程序的过程中，需要临时键入其他的命令，此时可以按`ctrl+z`，暂时将该程序置入背景中暂停，并且显示以下提示信息：

```
[1]+  Stopped                 vim nohup.out
```

### 查看后台程序的命令：jobs

```
jobs [-lrs]
-l ：除了列出job number和指令串以外，还将列出PID
-r ：仅列出正在运行的程序
-s ：仅列出被暂停的程序
```

### 将背景工作拿到前景来：fg

```
fg &jobnumber
jobnumber为背景工作的工作号码（通过jobs来看
```

### 让背景工作的状态变成运行中：bg

```
bg &jobnumber
jobnumber为背景工作的工作号码（通过jobs来看
```

### 管理背景中的工作：kill

```
kill -signal jobnumber
signal为给与后面那个Job哪种指示：
-1:表示重新读取一次配置参数（类似于reload
-2:相当于对Job采取ctrl+c的操作
-9:强制结束一个工作
-15:以正常的方式结束一个中工作
```

### 脱机管理：nohup 

```
nohup -command  & 
```

脱机管理与背景运行不同的是，当终端退出时，处于背景运行的程序会退出，若想将程序置于Linux主机后台运行则使用nohup命令

### 进程观察：ps

```
查阅自己的进程信息：ps -l
查阅主机的进程信息：ps aux（注意没有-）


F S   UID   PID  PPID  C PRI  NI ADDR SZ WCHAN  TTY          TIME CMD
4 S     0 25160 25154  0  80   0 - 28887 do_wai pts/0    00:00:00 bash
0 R     0 25363 25160  0  80   0 - 38338 -      pts/0    00:00:00 ps

```

1. F:进程标志(flag),4为root，1为表示该进程只能fork而不能exec
2. S：进程状态,
   1. R:正在运行中
   2. S：sleep正在处于睡眠状态，但是可以被唤醒
   3. D：表示被挂起，等待I/O输入
   4. T：停止状态，表示程序被暂停到背景中
   5. Z：Zombie，表示进程被终止，但是无法被移除至内存外
3. UID：用户ID，0为root
4. PID：进程ID
5. PPID：父进程ID
6. C：表示CPU占用率
7. PRI/NI：Priority/Nice，该值表示CPU执行的优先率，值越小表示优先级越高
8. ADDR：指出程序在内存的那个部分，若是running状态则表示`[-]`
9. SZ：表示程序使用了多少内存
10. WCHAN：表示程序是否在运行中，`[-]`表示正在运行
11. TTY：表示登入者的终端环境，pts/0表示由网络连接
12. TIME：表示CPU所消耗的时间
13. CMD：表示该进程被那个命令所触发



## SIGCHLD

当一个子进程改变了它的状态时（停止运行，继续运行或者退出），有两件事会发生在父进程中：

- 得到 SIGCHLD 信号；
- waitpid() 或者 wait() 调用会返回。

其中子进程发送的 SIGCHLD 信号包含了子进程的信息，比如进程 ID、进程状态、进程使用 CPU 的时间等。

在子进程退出时，它的进程描述符不会立即释放，这是为了让父进程得到子进程信息，父进程通过 wait() 和 waitpid() 来获得一个已经退出的子进程的信息。



## wait()

```
pid_t wait(int *status)
```

父进程调用 wait() 会一直阻塞，直到收到一个子进程退出的 SIGCHLD 信号，之后 wait() 函数会销毁子进程并返回。

如果成功，返回被收集的子进程的进程 ID；如果调用进程没有子进程，调用就会失败，此时返回 -1，同时 errno 被置为 ECHILD。

参数 status 用来保存被收集的子进程退出时的一些状态，如果对这个子进程是如何死掉的毫不在意，只想把这个子进程消灭掉，可以设置这个参数为 NULL。

## waitpid()

```
pid_t waitpid(pid_t pid, int *status, int options)
```

作用和 wait() 完全相同，但是多了两个可由用户控制的参数 pid 和 options。

pid 参数指示一个子进程的 ID，表示只关心这个子进程退出的 SIGCHLD 信号。如果 pid=-1 时，那么和 wait() 作用相同，都是关心所有子进程退出的 SIGCHLD 信号。

options 参数主要有 WNOHANG 和 WUNTRACED 两个选项，WNOHANG 可以使 waitpid() 调用变成非阻塞的，也就是说它会立即返回，父进程可以继续执行其它任务。

## 孤儿进程

一个父进程退出，而它的一个或多个子进程还在运行，那么这些子进程将成为孤儿进程。

孤儿进程将被 init 进程（进程号为 1）所收养，并由 init 进程对它们完成状态收集工作。

由于孤儿进程会被 init 进程收养，所以孤儿进程不会对系统造成危害。

## 僵尸进程

一个子进程的进程描述符在子进程退出时不会释放，只有当父进程通过 wait() 或 waitpid() 获取了子进程信息后才会释放。如果子进程退出，而父进程并没有调用 wait() 或 waitpid()，那么子进程的进程描述符仍然保存在系统中，这种进程称之为僵尸进程。

僵尸进程通过 ps 命令显示出来的状态为 Z（zombie）。

系统所能使用的进程号是有限的，如果产生大量僵尸进程，将因为没有可用的进程号而导致系统不能产生新的进程。

要消灭系统中大量的僵尸进程，只需要将其父进程杀死，此时僵尸进程就会变成孤儿进程，从而被 init 进程所收养，这样 init 进程就会释放所有的僵尸进程所占有的资源，从而结束僵尸进程。

### 动态查看进程信息：top

```
top -d 数字
top [-bnp]
```

参数：

1. top -d 数字：表示每隔几秒进行一次进程信息刷新
2. -b：以批次的方式进行top输出结果，还可以搭配管线命令输出到文件
3. -n：搭配-b，指明进行多少次
4. -p：指定某个PID进行观测

在top命令中还可以进行一些其他的命令操作，例如：

1. `'?'`：提示可以键入的命令
2. `'P'`以CPU使用的资源顺序进行排列
3. `'M'`以Memory使用的多少进行排序
4. `'N'`以进程PID进行排序
5. `'T'`有CPU累计的处理时间进行排序
6. `'k'`给PID一个讯号(signal)
7. `'r'`重新给PID一个Nice值
8. `'q'`离开top

### 列出进程树

```
pstree -A
```

### 进程的管理

通过signal来指挥进程做出相应的操作，即给进程一个讯号(signal)，常见的讯号为：

- SIGUP 1：重新读取配置文件，reload，重新启动
- SIGINT 2：相当于键入ctrl+c，来停止该进程
- SIGKIL 9：强制结束一个进程，若该进程正在运行，则有可能产生一个中间产物
- SIGTERM 15：以正常的方式结束一个进程，若进程正在执行，则当后续操作结束时，会正常退出
- SIGSTOP 19：相当于键入ctrl+z，来暂停该进程

### 将讯号传送给进程：kill 

```
kill -signal PID
```

### 通过指令名传送讯息至进程：killall

```
kill [-iIe] [command name]
```

参数：

1. `-i`：表示交互式，当需要删除时，会给消息提示给用户
2. `-I`：指令名忽略带小写
3. `-e`：exact（精确）的意思，表示指令名要完全一致

## 关于进程执行的顺序

priority和nice值：

priority，即PRI，表示进程的优先级，值越低表示进程的优先级越高

注意PRI，不能直接被调整（连root也不行？？），该值由内核根据nice值动态调整。规律大概如下：

```
PRI(new) = PRI(old) +nice
```

注意，nice值可以为正，表示时间越长优先级越低，也可以为负，表示时间越长优先级越高。

1. root可以调整其他人的nice值，root用户调整nice值的范围：`-20~19`
2. 而一般用户只能调整自己的nice值，可调整的范围：`0~19`
3. 且一般用户只能将nice值调高，即不能低于原来的nice值

### 执行一个新的命令，并赋予一个nice值

```
nice [-n 数字] command
```

### 调整一个程序的nice值

```
renice [number] PID 
```

## 系统资源的观察

### 观察内存的使用情况：free

```
free [-b|-k|-m|-g|-h] [-t] [-s num -c num]
```

1. 其中`[-bkmg]`表示显示的单位，即B、KB、MB、GB
2. `-t`，在最后显示物理内存和swap的总量（即total
3. `-s`，表示几秒输出一次的意思，后接数字
4. `-c`，表示让free 列出几次的意思

### 查看主机的系统与内核信息

```
uname
参数不列了，详情请man uname
```

### 追踪网络和插槽文件

```
netstat [-atunlp]
```

参数：

1. `'-a'`表示列出所有信息
2. `'-t'`表示列出tcp网络封包的信息
3. `'-u'`表示列出udp网络封包的信息
4. `'-n'`不以进程的服务名来列出信息，而已端口号来
5. `'-l'`列出正在网络监听的的服务
6. `'p'`列出该网络服务的进程ID

```
Proto Recv-Q Send-Q Local Address           Foreign Address         State      
tcp        0      0 iZi4sjttlk7jubZ:48384   123.56.121.160:mysql    ESTABLISHED
tcp        0      0 iZi4sjttlk7jubZ:48394   123.56.121.160:mysql    ESTABLISHED
tcp        0      0 iZi4sjttlk7jubZ:48386   123.56.121.160:mysql    ESTABLISHED
...
Active UNIX domain sockets (w/o servers)
Proto RefCnt Flags       Type       State         I-Node   Path
unix  2      [ ]         DGRAM                    10371    /run/systemd/shutdownd
unix  2      [ ]         DGRAM                    12471    /var/run/chrony/chro
```

分为两个部分，一个为网络的联机方面，一个为非网络（即Linux内部的socket进程相关部分）

网络联机方面参数：

1. Proto:网络封包协议
2. Recv-Q：有用户程序连接到此socket总共复制的byte总数
3. Send-Q：从远程主机总共接收的Acknowledged的byte总数
4. local address：本地主机IP和端口
5. Foreign Address：远程主机的端口
6. 连接状态：

### 借由文件查看正在使用该文件的进程

```
fuser 	[-umv] [-k -i -signal] dir/filename
```

参数：

1. `'-u'`：除了PID还列要列出用户名
2. `'-m'`
3. `'-v'`：列出该文件与进程的完整相关性
4. `'[-k -i -signal]'`：表示将讯息传送给查找到的与文件相关的进程，-i表示删除前的确认

### 列出被进程开启的文档名：

```
lsof [-aUu] [+d]
```

参数：

1. `'-a'`：当数据需要多项同时成立时
2. `'-u'`：后面可接需要查看的用户名
3. `'-U'`：仅显示 Unix Like系统的socket文件
4. `'+d'`：后面接目录，即表示该目录下被进程打开的文件

### 列出某个正在执行的程序的PID

```
pidof [-sx] program_name
```

参数：

1. `'-s'`仅列出一个PID，而不列出所有的PID
2. `'-x'`若PPID存在，则列出PPID的PID

## SELinux

SELinux：Security Enhanced Linux，安全强化Linux。背景是由美国安全局研发，该项目起初的开发目的是在大多数企业的经验中，发现系统出现故障的原因往往是内部员工的误操作，而不是来自外部的攻击。

SELinux意义：SELinux是对进程、文件等设定细部权限的一个安全模块。主要作为控制网络行为是否能够获取系统资源的一道关。

### DAC和MAC：

DAC：自主式控制访问模式，此模式的弊端是

1. 一些比较重要的文件即使设置了权限，对于root也是无用的。
2. 对于一些使用者来言（内部员工），如果不小心通过进程修改了文件的读写权限，那这样很危险。

MAC：委任式访问模式，如下图

![image-20200609175129317](.\Linux入门笔记12进程管理与SELinux.assets\image-20200609175129317.png)

## SELinux模式

主要组成部分：

1. 主体（subject）：即进程
2. 目标（object）：即文件系统
3. 政策（policy）：存取安全性政策CentOS7.x提供了主要的三个政策：
   1. target：主要针对网络服务，对本机的限制较少，是预设的政策
   2. minimum：由target修改而来，仅针对选择的进程来保护
   3. mls：完整的SELinux限制
4. 安全性文本（security context）：主体和目标的安全性文本必须保持一致，才能读取

![image-20200609192143833](.\Linux入门笔记12进程管理与SELinux.assets\image-20200609192143833.png)

### 安全性文本

安全性文本主要分为三个字段

```
Identify：role：type
身份识别： 角色： 类型
```

1. 身份识别，比较常见的有以下：

   1. unconfined_u：不受限的用户，一般在登录得到的bash不受到SELinux的管制，因为bash并没有用到什么网络服务，故bash进程产生的文件的身份大多数都是unconfined_u
   2. system_u：为系统本身创建的文件，网络服务的身份大多数也属于system_u

2. 角色，是指文件、进程还是一般用户

   1. object_r：指文件或目录
   2. system_r：指进程，不过一般用户也是system_r

3. 类型:

   类型相对于identify和role更加重要，一个进程能否读到文件资源，往往就和type有关。

   注意类型在进程和文件中的定义不太一样：

   1. type：在文件资源中定义为type
   2. domain：在进程中则称为领域

![image-20200609201045069](.\Linux入门笔记12进程管理与SELinux.assets\image-20200609201045069.png)

上述有两个重点：

1. 一是要定制详细的domain/type相关性
2. 如果type定义错了，即使文件的权限为777，那也读取不了

### SELinux的启动模式

1. enforcing:强制模式，不能读写的就是不能读写！
2. permissive：宽容模式，不能读写的话会有警告，一般用于debug测试
3. disable：禁止模式，禁用SELinux

### 关于SELinux的一些命令：

```
//显示当前SELinux的模式
getenforce

//列出当前带SELinux参数的进程
ps -eZ

//查看SELinux政策
sestatus

//将Linux在permissive和enforce中转换
//0表示permissive，1表示enforce
setenforce [0|1]

//获取SE的规则
//参数：-a:列出所有SELinux规则的bool
getsebool [-a]
```

### 查询SELinux规则

命令：seinfo

```
seinfo [-Atrub]
参数：
-A :列出SELinux的所有信息，包括状态、规则布尔值、身份、角色、类别
-u :列出SELinux的身份识别（user)
-r :列出SELinux的角色(role)
-t :列出SELinux的类别(type)
-b :列出所有规则的种类（布尔值)
```

命令：sesearch

```
sesearch [-A] [-s 主体类别] [-t 目标类别] [-b 规则的布尔值]
-A ：列出后面数据中，允许[读取或放行]的相关数据
-t ：后面还要接类别，例如 -t httpd_t
-b ：后面还要接SELinux的规则，例如 -b httpd_enable_ftp_server
```

命令：setsebool（建议使用时一定要加上`-P`的选项

```
setsebool [-P] [规则名称] [0|1]
-P ： 直接将设定写入配置文件，该设定数据未来会生效
```

命令：chcon（手动修改文件的SELinux

```
chcon [-R] [-t type] [-u user] [-r role] 文件
chcon [-R] --reference=范例文件 文件
-R ：递归，将此目录下的次目录同时修改
-t ：后接安全性文本的类型字段，如httpd_sys_content_t
-u ：后接身份识别，如system_u
-r ：后接角色，如system_r
-v ：若有变化成功，则将结果列出来
--reference=规范文件，将某文件的SELinux type套用到该文件上
```

命令：restorecon（让文件恢复到预设的SELinux

```
restorecon [-Rv] 文件或目录
-R ： 连同目录一起修改
-v ：将过程显示到屏幕上
```

