# 计算机概论

CPU作为一个具有特定功能的芯片，里头含有微指令集，CPU分为两个主要的单元，分别为算数逻辑单元和控制单元

CPU架构：

1. 精简指令集（Reduced Instruction Set Computer:RISC)：执行效率佳，但对于复杂的任务需要多个指令来完成，常见RISC类型CPU为甲骨文的SPARC，IBM的PowerPC，安谋的ARM CPU
2. 复杂指令集（Complex Instruction Set Computer:CISC)：花费的时间长，但每条个别的指令处理的工作丰富，常见CPU包括：AMD、Inter、VIA等的x86架构CPU



![image-20200603125828425](.\Linux入门笔记 00计算机概论.assets\image-20200603125828425.png)

操作系统主要负责的就是中间核心以及系统呼叫这两层



![image-20200603132126364](E:\homework\Markdown\temp\2.png)

Linux通常指Linux kernal（LInux核心），而我们平常使用的是LInux发布版（kernal+tools+softwares+可安装程序）

### Linux 入门要素

1. 计算机概论和硬件相关知识
2. Linux安装命令
3. Linux操作系统基础技能
4. vi文本编辑器
5. Shell和Shell文本编辑器
6. 软件管理

### 磁盘分区有点：

1. 出于安全性考虑，当需要格式化系统盘时，存储其他数据文件的其他盘不会受到影响
2. 系统效率考虑：若要读取其中一个磁盘的文件时，只要搜索该磁盘的磁柱范围，而非搜索全磁盘

### UEFI

统一可扩展固件接口（Unified Extensible Firmware interface）用来定义操作系统与系统固件之间的软件界面，作为BIOS的替代方案

### man命令

man命令之后数字所代表的意思

![image-20200603181558653](E:\homework\Markdown\temp\3.png)

