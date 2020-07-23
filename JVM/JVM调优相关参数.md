## JVM调优



![img](https://github.com/liu8926847/MyMarkDown/blob/master/JVM/img/99109d10-f638-4922-aeb6-0a353c3d1e86.png)



#### 堆参数

![img](https://github.com/liu8926847/MyMarkDown/blob/master/JVM/img/java_jvm_heap_parameters.png)

JDK1.8遗弃了永久代改为使用元空间：

```
-XX:MetaspaceSize=N //设置 Metaspace 的初始（和最小大小）
-XX:MaxMetaspaceSize=N //设置 Metaspace 的最大大小，如果不指定大小的话，随着更多类的创建，虚拟机会耗尽所有可用的系统内存。
```



#### 回收器参数

![img](https://github.com/liu8926847/MyMarkDown/blob/master/JVM/img/java_jvm_garbage_collector_parameters.png)



![img](https://github.com/liu8926847/MyMarkDown/blob/master/JVM/img/java_jvm_compose_garbage_collector.png)

#### 常用参数

![img](https://github.com/liu8926847/MyMarkDown/blob/master/JVM/img/java_jvm_suggest_parameters.png)



### GC 调优原则

在调优之前，我们需要记住下面的原则：

> 多数的 Java 应用不需要在服务器上进行 GC 优化； 多数导致 GC 问题的 Java 应用，都不是因为我们参数设置错误，而是代码问题； 在应用上线之前，先考虑将机器的 JVM 参数设置到最优（最适合）； 减少创建对象的数量； 减少使用全局变量和大对象； GC 优化是到最后不得已才采用的手段； 在实际使用中，分析 GC 情况优化代码比优化 GC 参数要多得多。

### GC 调优目的

将转移到老年代的对象数量降低到最小； 减少 GC 的执行时间。

### GC 调优策略

**策略 1：**将新对象预留在新生代，由于 Full GC 的成本远高于 Minor GC，因此尽可能将对象分配在新生代是明智的做法，实际项目中根据 GC 日志分析新生代空间大小分配是否合理，适当通过“-Xmn”命令调节新生代大小，最大限度降低新对象直接进入老年代的情况。

**策略 2：**大对象进入老年代，虽然大部分情况下，将对象分配在新生代是合理的。但是对于大对象这种做法却值得商榷，大对象如果首次在新生代分配可能会出现空间不足导致很多年龄不够的小对象被分配的老年代，破坏新生代的对象结构，可能会出现频繁的 full gc。因此，对于大对象，可以设置直接进入老年代（当然短命的大对象对于垃圾回收来说简直就是噩梦）。`-XX:PretenureSizeThreshold` 可以设置直接进入老年代的对象大小。

**策略 3：**合理设置进入老年代对象的年龄，`-XX:MaxTenuringThreshold` 设置对象进入老年代的年龄大小，减少老年代的内存占用，降低 full gc 发生的频率。

**策略 4：**设置稳定的堆大小，堆大小设置有两个参数：`-Xms` 初始化堆大小，`-Xmx` 最大堆大小。

**策略5：**注意： 如果满足下面的指标，**则一般不需要进行 GC 优化：**

> MinorGC 执行时间不到50ms； Minor GC 执行不频繁，约10秒一次； Full GC 执行时间不到1s； Full GC 执行频率不算频繁，不低于10分钟1次。



#### GC记录

为了严格监控应用程序的运行状况，我们应该始终检查JVM的*垃圾回收*性能。最简单的方法是以人类可读的格式记录*GC*活动。

使用以下参数，我们可以记录*GC*活动：

```
-XX:+UseGCLogFileRotation 
-XX:NumberOfGCLogFiles=< number of log files > 
-XX:GCLogFileSize=< file size >[ unit ]
-Xloggc:/path/to/gc.log
```