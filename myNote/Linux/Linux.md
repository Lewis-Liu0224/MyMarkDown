## EXT2文件系统（inode)

当文件大小超过百GB时，inode和block放在一起不合适，故在ext2系统中，基本使用的是区块群组（block group)，每个block group都有自己的 inode/block/superblock

![image-20200604133600727](E:\homework\Markdown\myNote\Linux\Linux.assets\image-20200604133600727.png)

## 关于block group

一般将datablock和inodetable叫做数据存放区，superblock、inode bitmap和block bitmap叫做中介数据区（每次新增、移除和编辑都会影响该数据区）

1. Data Block

   Block在格式化时，大小和编号已经固定，且支持的大小为1K，2K，4K，因支持的大小不一样，导致系统的单一文件最大限制，和文件系统总容量限制不同，具体不同入下表:

   ![image-20200604134218161](E:\homework\Markdown\myNote\Linux\Linux.assets\image-20200604134218161.png)

   此外，如果一个文件的大小小于该block大小，则该block剩余空间被浪费

2. Inode table

   Inode的内容基本如下：

   1. 该文件的存取模式（rwx
   2. 该文件的拥有者和群组
   3. 该文件的大小
   4. 该文件的（最近一次修改时间）mtime、（最近读取时间）atime、(文件建立或状态改变世界)ctime
   5. 该文件的特性（如SUID.....
   6. 该文件真正内容的指向

   Inode的属性如下：

   1. 每个inode大小为128byte（xfs和ext4大小可为256byte
   2. 每个文件定义一个node
   3. 因上两点，该文件系统能够建立的文件数量与inode的数量有关
   4. 读文件，需先找到inode，根据用户与inode的权限是否符合，再判断是否进行实际读取block内容

   为了节省inode数量，遇到特别大的文件时，文件系统使用的是间接记录区（即用Inode指向的block存储block信息），同理双间接记录区（即即用Inode指向的block存储指向block的block），同理三间接记录区

3. SuperBlock

   SuperBlock记录的信息为

   1. block和inode的总量，剩余量，已使用量
   2. block和inode的大小（block为1K、2K、4K，inode为128byte,256byte）
   3. filesystem的挂载时间、最近一次写入时间、最近一次检验磁盘的时间、文件系统相关信息
   4. 一个Valid bit值，为0表示未挂载，为1表示已挂载

4. FileSystem Description

   描述每个groupblock的开始和结束block号码，以及说明每个区段（superblock、bitmap、inodeMap、data block）位于哪个block之间

   ![image-20200604143516315](E:\homework\Markdown\myNote\Linux\Linux.assets\image-20200604143516315.png)

5. block bitmap

   在处理新的文件时，需要block来进行存储，而block bitmap的作用是快速告诉系统哪些是空的block。当删除文件时，则要释放相关的block号码

6. inode bitmap

   同理与block bitmap ，该区段指的是inode

## 进程状态

| 状态 | 说明                                                         |
| :--: | ------------------------------------------------------------ |
|  R   | running or runnable (on run queue)<br>正在执行或者可执行，此时进程位于执行队列中。 |
|  D   | uninterruptible sleep (usually I/O)<br>不可中断阻塞，通常为 IO 阻塞。 |
|  S   | interruptible sleep (waiting for an event to complete) <br> 可中断阻塞，此时进程正在等待某个事件完成。 |
|  Z   | zombie (terminated but not reaped by its parent)<br>僵死，进程已经终止但是尚未被其父进程获取信息。 |
|  T   | stopped (either by a job control signal or because it is being traced) <br> 结束，进程既可以被作业控制信号结束，也可能是正在被追踪。 |

<br>

<div align="center"> <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/2bab4127-3e7d-48cc-914e-436be859fb05.png" width="490px"/> </div><br>

## SIGCHLD

当一个子进程改变了它的状态时（停止运行，继续运行或者退出），有两件事会发生在父进程中：

- 得到 SIGCHLD 信号；
- waitpid() 或者 wait() 调用会返回。

其中子进程发送的 SIGCHLD 信号包含了子进程的信息，比如进程 ID、进程状态、进程使用 CPU 的时间等。

在子进程退出时，它的进程描述符不会立即释放，这是为了让父进程得到子进程信息，父进程通过 wait() 和 waitpid() 来获得一个已经退出的子进程的信息。

<div align="center"> <!-- <img src="https://cs-notes-1256109796.cos.ap-guangzhou.myqcloud.com/flow.png" width=""/> --> </div><br>

## wait()

```c
pid_t wait(int *status)
```

父进程调用 wait() 会一直阻塞，直到收到一个子进程退出的 SIGCHLD 信号，之后 wait() 函数会销毁子进程并返回。

如果成功，返回被收集的子进程的进程 ID；如果调用进程没有子进程，调用就会失败，此时返回 -1，同时 errno 被置为 ECHILD。

参数 status 用来保存被收集的子进程退出时的一些状态，如果对这个子进程是如何死掉的毫不在意，只想把这个子进程消灭掉，可以设置这个参数为 NULL。

## waitpid()

```c
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