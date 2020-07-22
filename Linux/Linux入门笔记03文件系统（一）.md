# Linux入门笔记03文件系统(一)

## 文件系统特性

Linux中，一个文件或一个目录除了实际的数据外，还有很多属性例如权限、所属、时间等，因此在文件系统中常常会将这两部分放入不同的地方。比如

1. 文件的属性放入inode区块中，一个文件占用一个inode，同时会记录文件对应的block号
2. 实际的数据放入data block区块中，若数据太多则会占用多个block
3. 此外还有一个`superblock`会记录整个文件系统的整体信息，比如说inode和datablock的总量、使用量以及剩余量

![image-20200604133218391](.\Linux入门笔记 03.assets\image-20200604133218391.png)

## EXT2文件系统（inode)

当文件大小超过百GB时，inode和block放在一起不合适，故在ext2系统中，基本使用的是区块群组（block group)，每个block group都有自己的 inode/block/superblock

![image-20200604133600727](.\Linux入门笔记 03.assets\image-20200604133600727.png)

## 关于block group

一般将datablock和inodetable叫做数据存放区，superblock、inode bitmap和block bitmap叫做中介数据区（每次新增、移除和编辑都会影响该数据区）

1. Data Block

   Block在格式化时，大小和编号已经固定，且支持的大小为1K，2K，4K，因支持的大小不一样，导致系统的单一文件最大限制，和文件系统总容量限制不同，具体不同入下表:

   ![image-20200604134218161](.\Linux入门笔记 03.assets\image-20200604134218161.png)

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

   ![image-20200604143516315](.\Linux入门笔记 03.assets\image-20200604143516315.png)

5. block bitmap

   在处理新的文件时，需要block来进行存储，而block bitmap的作用是快速告诉系统哪些是空的block。当删除文件时，则要释放相关的block号码

6. inode bitmap

   同理与block bitmap ，该区段指的是inode

## 目录在文件系统中的存储

Linux目录区别于文件，系统会分配一个inode和至少一个block给目录，该inode记录目录的属性，block则记录该目录下的文件的inode（如下图）

![image-20200604143918518](.\Linux入门笔记 03.assets\image-20200604143918518.png)

## 日志文件系统

ext2文件系统在发生意外特殊情况时（例如断电等...），会导致数据在datablock和inodetable存储完毕，而中介数据区段还未更新的特殊情况（称为不一致现象Inconsistent），此时系统的措施是在重启时通过是否挂载来判断是否进行数据一致性检查，对于数据较大的服务器情况下，该检查十分耗时。

针对于上述的ext2的不一致现象，有了记录日志文件的办法，在filesystem中规划出一块区域，用来记录写入和修改文件的步骤，于是则简化了一致性检查：

![image-20200604150631831](.\Linux入门笔记 03.assets\image-20200604150631831.png)

ext3和ext4已具备该功能

## Linux VFS（Virtual File System ）虚拟档案系统

不同的dev可能使用的文件系统不一样，此时Linux通过Virtual File System Switch的核心功能来识别不同的文件系统，可以理解为LInux通过VFS来管理所有的filesystem

![image-20200604151951757](.\Linux入门笔记 03.assets\image-20200604151951757.png)

## Linux XFS文件系统

ext文件系统短板：对于大容量磁盘来说，格式化速度非常慢，被如今越来越大容量的趋势所淘汰。

XFS文件系统划分为3块：资料区（data section)、文件活动登录区（log section)、以及一个实时运作区（realtime section）

1. 资料区：

   和ext一样包括，inode、block、superblock。并且有block group 。每个存储群组还包含了1.群组superblock2.剩余空间管理机制3.inode的分配和追踪。并且，只有在需要用到block和inode时才动态配置产生，故格式化的速度较快

2. 文件活动登录区：

   该区用来记录文件变化并登记，在文件系统损坏时（例如断电），文件系统通过该区来恢复。此区域磁盘活动较为频繁，故还可以指定外部磁盘来当做文件活动的登录区（例如用一块SSD）来加快速度

3. 实时运作区

   当有档案要被建立时，xfs 会在这个区段里面找一个到数个的extent 区块，将档案放置在这个区块内，等到分配完毕后，再写入到data section 的inode 与block中。这个 extent 区块的大小得要在格式化的时候就先指定，最小值是 4K 最大可到 1G。一般非磁碟阵列的磁碟预设为 64K 容量，而具有类似磁碟阵列的 stripe 情况下，则建议 extent 设定为与 stripe 一样大较佳。这个 extent 最好不要乱动，因为可能会影响到实体磁碟的效能。

## XFS文件系统信息

![image-20200604154116533](.\Linux入门笔记 03.assets\image-20200604154116533.png)

### 使用`df`命令来查询文件系统

![image-20200604155136815](.\Linux入门笔记 03文件系统.assets\image-20200604155136815.png)

## 实体链接和符号链接

在Linux下的连接有两种：

1. 一种是符号链接，类似于Windows中的快捷方式，是建立一个新文件来指向被连接文件
2. 一种是实体链接，通过文件系统的inode链接来产生新档名（看完这部分知识后，回来吐槽一下，这个档名不就是指针吗qwq)

重点是实体链接，简单来说hardlink是新增一个链接到某个inode号码的一个记录。例如

![image-20200604185509647](.\Linux入门笔记 03文件系统.assets\image-20200604185509647.png)

由上图可知：

1. 因为两个文件档名指向同一个inode，故这两个文件的相关信息完全一样（除了名字）
2. 该文件的第二个字段由1变为了2，该字段表示有多少个档名连接关联到了该inode号

![image-20200604185807650](.\Linux入门笔记 03文件系统.assets\image-20200604185807650.png)

### 建立连接的命令：

![image-20200604190610630](.\Linux入门笔记 03文件系统.assets\image-20200604190610630.png)

### 关于目录的Link数量

若新建一个目录（例如/tmp/testing)时，基本会有三个东西：

- `/tmp/testing`
- `/tmp/testing/.`
- `/tmp/testing/..`

而`/tmp/testing`和`/tmp/testing/.`其实指向的是同一个inode，故新建的一个文件夹其连接数为2

![image-20200604191812421](.\Linux入门笔记 03文件系统.assets\image-20200604191812421.png)

此外由于有一个`/tmp/testing/..`故`/tmp`的连接数会加1

![image-20200604191909179](.\Linux入门笔记 03文件系统.assets\image-20200604191909179.png)![image-20200604191916985](.\Linux入门笔记 03文件系统.assets\image-20200604191916985.png)

