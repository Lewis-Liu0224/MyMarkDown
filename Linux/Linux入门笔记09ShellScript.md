# Shell Script程序化脚本

看到一半，回来吐槽下，以为是学操作系统，但还是要学编写程序(qwq!!)

脚本的一些规则和注意：

1. 脚本的读取顺序是从上到下，从左到右
2. 指令和参数间的空白将会被忽略
3. 空白行将被忽略
4. '#'作为注释
5. 读取到Enter键，就会尝试开始执行命令，故用`[\Enter]`来换行
6. 关于脚本的执行:
   1. 通过绝对路径来执行
   2. 若已在目录下，则通过`./shell.sh`
   3. 将脚本放到PATH内的路径
   4. 通过bash shell.sh或sh shell.sh来执行程序

```shell
#!/bin/bash
#Program
# show 'Hello World in your Screen'
#History
#2020年6月7日   	lius	First release
PATH=/usr/local/bin:/usr/local/sbin:/usr/bin:/usr/sbin:/bin:/sbin:/home/liu/.local/bin:/home/liu/bin
export PATH
echo -e "Hello World \a \n"
exit 0
```

程序说明：

1. 第一行为固定写法，代表此script使用的Shell
2. #后面为注释，要有良好的注释习惯，写上此程序的说明、联系人、修改时间、联系方式等
3. PATH和export 为环境变量的声明和引入，这样方便程序内引入外部的指令，而不用使用绝对路径来引入指令
4. 程序主体
5. exit n 为自定义回传值，在执行完之后通过`$?`来获取此值

## 用不同方法来执行脚本程序

1. 直接运行script：运行之后，此程序与bash的关系为子程序和父程序的关系，当子程序运行完之后，子程序中的变量便会自动销毁，即在父程序不会得到浙西变量

2. 以source的方式运行script：

   ```
   source shell.sh
   ```

   以此种方式运行脚本则不同，该程序会在父程序中直接运行，并且父程序能够获得其变量

## 善用判断式

除了使用回传值，还可以使用`test `来进行条件判断，并返回结果

1. 关于某个档名的存在类型进行判断，例如`test -e filename`表示filename存在与否
   1. `-e`判断该文件是否存在
   2. `-f`判断该存在且为file
   3. `-d`判断是否存在且为目录dir?
   4. `-b`判断是否存在且为blockdevice
2. 关于权限的检查，如`test -r filename`来判断对该文件是否有读的权限
   1. `-r`略
   2. `-w`略
   3. `-x`略
   4. `-u`判断是否存在且具有`[SUID]`的属性
   5. `-g`判断是否存在且具有`[SGID]`的属性
   6. `-k`判断是否存在且具有`[Sticky bit]`的属性
   7. `-s`判断是否存在且为`[非空白文件]`
3. 关于文件的比较，
   1. `[-nt]`：(newer than)判断file1是否比file2新
   2. `[-ot]`：(older than)判断file2是否比file2旧
   3. `[-ef]`:判断两个文件是否为同一个文件，判断的依据为两个文件是否指向同一个inode
4. 整数间的判断
   1. `[-eq]`：equal
   2. `[-ne]`：not equal
   3. `[-gt]`：great than
   4. `[-lt]`：less than
   5. `[-ge]`great than or equal
   6. `[-le]`less than or equal
5. 字符串的判断
   1. `test -z string`判断string是否为空字符串
   2. `test -n string`判断string是否为非空字符串
   3. `test str1==str2`判断两个字符串是否相等
   4. `test str1!=str2`判断两个字符串是否非等
6. 多重条件判断
   1. `-o`表示`or`，`test -r file1 -o -x file2`判断该文件是否拥有读写中的一种权限
   2. `-a`表示`and `， `test -r file1 -a -x file2 `判断该文件是否同时拥有读写中的一种文件
   3. `!`表示非，太简单，略了 = = 

## script中的参数

我们常常使用的命令后面往往是跟参数的例如`services mysql start`，这个start就是参数，除了start还可以是stop、restart。

在程序编写的时候，我们则可以使用：${0}，${1}，${2}，${3}....来获取，其中${0}表示该文件名，而${1}表示第一个参数，${2}表示第二个参数等。

此外程序还内建了一些其他关于这些参数的变量，例如：

1. $#，表示这些参数的个数
2. $@，列出这些参数

## 使用if...then

基本的使用：

```
if [ 条件判断式 ]; then
		//todo
fi
```

在条件判断式中就可以使用`'&&'和'||'`了

```
if [ 条件1 ] || [ 条件2 ]
```

### if...then的扩展使用

```
if [ 条件 ]; then
	//todo
else 
	//todo
fi
```

还可以这样

```
if [ 条件1 ]; then
	//todo
elif [ 条件2 ]; then
	//todo
else 
	//todo
fi
```

## 使用case...esac 判断

基本用法

```
case $变量名 in
	"case1")
		//todo
		;;//每个case固定的结尾
	"case2")
		//todo
		;;
	*)//若以上的都没执行
		//todo
		;;
esac	//case语句的结尾
```

## 函数function

注意，因为script的读取顺序是从上到下，故function应该写在前面

```
function f(){
	//todo
}
```

## 循环（loop）

1. 第一个当然是while了（又是你 = =。 

```
while [ 条件判断 ]
do
		//todo
done
```

until和while相反，若条件成立则跳出循环

```
until [ 条件判断 ]
do
		//todo
done
```

## for循环

第一种写法(有点类似于foreach)：

```
for var in con1 con2 con3 ...
do
		//todo
done
```

每次循环将con1赋给var，这种写法con1、con2...要一个个写，很不方便

故可以这样（类似于Python 里的for了）：

```
for var in $(seq 1 100)
do
		//todo
done
```

还可以这样：（兄台，终于看到李了QAQ）

```
for((初始值;限制值;提升阶数))
do
		//todo
done
```

## ShellScript Debug

debug主要使用此命令:

```
sh [-nvx] shell.sh
```

参数介绍：

1. `'-n'`：不执行script，仅检查语法
2. `'-v'`：先将script内容输出到屏幕上，再执行文件
3. `'-x'`：调试script，一边执行一边显示（只不过不能打断点？？