# Linux账号管理和ACL权限

## Linux的账号和群组

### 使用者标识符UID 和GID

每个登录者至少有两个ID，一个是使用者ID（User ID ，UID）和群组ID （Group ID ，GID）

### etc/passwd 文件结构

```
root:x:0:0:root:/root:/bin/bash  <==等一下做為底下說明用
bin:x:1:1:bin:/bin:/sbin/nologin
daemon:x:2:2:daemon:/sbin:/sbin/nologin
adm:x:3:4:adm:/var/adm:/sbin/nologin
```

一行由`':'`隔开，每个字段分别代表：

1. 账号名称
2. 密码，早期Unix密码就放在这里，因为此文件所有程序都能读取，所有才将密码放到/etc/shadow里了，这里显示的是`[x]`
3. UID：即使用者标识符，不同的UID也有含义：
   1. `0`代表root
   2. `1~999`代表系统账号
      1. `1~200`：由distribution自行创建的账号
      2. `201~999`若用户有系统账号需求时，可以使用的账号
   3. `1000~60000`给一般使用者
4. GID：group id
5. 用户信息说明栏
6. 家目录，默认的家目录在`/home/账号名称`
7. 指定的shell，不同的用户在登录之后都会获得一个shell来和核心进行沟通。此外有一个特殊的shell，可以让用户无法获得shell，即/sbin/nologin

### /etc/shadow文件结构

```
root:$6$wtbCCce/PxMeE5wm$KE2IfSJr.YLP7Rcai6oa/T7KFhO...:16559:0:99999:7:::  <==底下說明用
bin:*:16372:0:99999:7:::
daemon:*:16372:0:99999:7:::
adm:*:16372:0:99999:7:::
```

同样以`':'`作为分隔符，每个字段意思如下：

1. 账号名称
2. 密码：该栏是经过加密之后的密码
3. 最近更改密码的日期：距离1970年1月1日，以1日作为累加的时间
4. 密码不可变更的时间，表示在修改过后，需要多少天后才能修改，0表示随时都能修改
5. 密码需要重新变更的天数，表示必须在这个天数之内修改，否则变为过期特性
6. 密码需要提前变更的警告
7. 密码过期后的宽恕天数（过了期还能赊账吗= = ！！
8. 账号时效日（用于收费服务
9. 保留

## 关于群组

### /etc/group 文件结构

```
root:x:0:
bin:x:1:
daemon:x:2:
sys:x:3:
```

1. 账号名称
2. 密码
3. GID
4. 此群组支持的账号名称，好像是该群组的成员？？

### 初始群组和有效群组

当一个账号加入了多个群组时，通过

```
groups
```

命令，得出的群组中，排列顺序第一个的即是有效群组。

有效群组的意义在于，当用户创建文件时，其所属组即该用户的有效群组

切换有效群组的命令：

```
newgrp 组名
```

### 群组管理员

/etc/gshadow的内容

```
root:::
bin:::
daemon:::
sys:::
```

以冒号分隔，其中：

1. 组名
2. 密码栏
3. 管理员账号
4. 加入该群组所属账号

## 账号管理

### 新增与移除用户

```
useradd [-u UID] [-g 初始群组] [-G 次要群组] [-mM]\> [-c 说明栏] [-d 家目录绝对路径] [-s shell] 使用者帐号名选项与参数：
-u ：后面接的是 UID ，是一组数字。直接指定一个特定的 UID 给这个帐号；
-g ：后面接的那个群组名称就是我们上面提到的 initial group该群组的 GID 会被放置到 /etc/passwd 的第四个栏位内。
-G ：后面接的群组名称则是这个帐号还可以加入的群组。这个选项与参数会修改 /etc/group 内的相关资料喔！
-M ：强制！不要建立使用者家目录！ (系统帐号预设值)
-m ：强制！要建立使用者家目录！ (一般帐号预设值)
-c ：这个就是 /etc/passwd 的第五栏的说明内容啦～可以随便我们设定的啦～
-d ：指定某个目录成为家目录，而不要使用预设值。务必使用绝对路径！
-r ：建立一个系统的帐号，这个帐号的 UID 会有限制 (参考 /etc/login.defs)
-s ：后面接一个 shell ，若没有指定则预设是 /bin/bash 
-e ：后面接一个日期，格式为『YYYY-MM-DD』此项目可写入 shadow 第八栏位亦即帐号失效日的设定项目
-f ：后面接 shadow 的第七栏位项目，指定密码是否会失效。 0为立刻失效，      
-1 为永远不失效(密码只会过期而强制于登入时重新设定而已。)
```

## ACL的使用（Access Control List）

### ACL使用者的设定

```
setfacl [-bkRd] [{-m|-x} acl参数] 目标文件名
```

参数介绍：

1. `'-m'`设定后续ACL参数
2. `'-x'`删除后续的ACL参数
3. `'-b'`移除所有的ACL参数
4. `'-k'`移除预设的ACL参数
5. `'-R'`递归的设定ACL参数
6. `'-d'`设定预设的ACL参数

```
 [{-m|-x} acl参数] 
 acl参数的规范为：[u:[使用者账号的列表]:[rwx]]
```

### ACL使用者的获取

```
getfacl
```

参数与setfacl参数相同

### ACL群组的设定

```
设定和使用者的ACL设定相似
setfacl -m g:group1:rx filename
```

## ACL mask的设定

mask可以理解为该文件的权限上限，即

```
最终权限 = 使用者权限 && mask规定的权限
或
最终权限 = 使用者权限 && mask规定的权限
```

用法和上面也类似，只不过少了用户和群组对象了，mask对象为文件

```
setfacl -m m:r filename
```

### 预设目录的ACL权限

通过预设目录的ACL，可以使目录下的子文件继承该目录的ACL属性

用法：

```
setfacl -m d:[u|g]:[user|group]:rwx
```

## 切换用户

### su

命令：

```
su [-lm] [-c 指令] [username]
参数介绍：
若不加参数，则表示登录root
-l 参数跟username
-m 同样也为登录，只不过表示使用当前的环境变量，而不读取新的配置文件
[-c 命令]表示仅进行一次命令，通常配合root使用
```

### sudo

因为安全原因，root密码不方便在多人环境下传播，故有sudo这一命令，代表以root的权限执行接下来的命令

sudo能否执行的重点在于`/etc/sudoers`中的设定值，若使用者并没有在文件设定则使用不了，其次若能够使用还需要再次输入使用者的密码，才能真正使用

## PAM模块介绍

PAM（Pluggable Authorization Modules）可以理解为一个应用程序接口，提供了一系列验证服务，使用者将验证阶段的需求告诉PAM之后，PAM能够汇报使用者验证的结果

下面是passwd调用PAM的流程：

1. 使用者开始执行 /usr/bin/passwd 这支程式，并输入密码；
2. passwd 呼叫 PAM 模组进行验证；
3. PAM 模组会到 /etc/pam.d/ 找寻与程式 (passwd) 同名的设定档；
4. 依据 /etc/pam.d/passwd 内的设定，引用相关的 PAM 模组逐步进行验证分析；
5. 将验证结果 (成功、失败以及其他讯息) 回传给 passwd 这支程式；
6. passwd 这支程式会根据 PAM 回传的结果决定下一个动作 (重新输入新密码或者通过验证！)

### 具体验证步骤：

以`/etc/pam.d/passwd`为例

```
[root@study ~]# cat /etc/pam.d/passwd
#%PAM-1.0  <==PAM版本的說明而已！
auth       include      system-auth   <==每一行都是一个验证的过程
account    include      system-auth
password   substack     system-auth
-password   optional    pam_gnome_keyring.so use_authtok
password   substack     postlogin
验证类别   控制标准     PAM 模组与该模组的参数
```

1. 第一个字段：type，验证的类别
   1. auth:即authorization，后续往往就是用来检验用户的
   2. accout：即检验使用者是否拥有真正的权限，后面则需要密码来校验
   3. session
   4. password
2. 第二个字段:flag，验证通过的标准
   1. required：无论验证成功或者失败都会继续后面的验证流程，这常常用于log记录
   2. requisite：若验证失败则，终止验证流程。若成功则继续下面的验证流程
   3. sufficient：与requisite相反，若验证成功则终止验证流程。若失败则继续下面的流程
   4. optional：与验证无关，用来显示讯息

## PAM常用模块

以下是常用模块的位置

/etc/pam.d/：每个程式个别的 PAM 设定档；
/lib64/security/：PAM 模组档案的实际放置目录；
/etc/security/：其他 PAM 环境的设定档；
/usr/share/doc/pam-/：详细的 PAM 说明文件。

一些常用模块

pam_securetty.so：
限制系统管理员 (root) 只能够从安全的 (secure) 终端机登入；那什么是终端机？例如 tty1, tty2 等就是传统的终端机装置名称。那么安全的终端机设定呢？就写在 /etc/securetty 这个档案中。你可以查阅一下该档案， 就知道为什么 root 可以从 tty1~tty7 登入，但却无法透过 telnet 登入 Linux 主机了！

pam_nologin.so：
这个模组可以限制一般使用者是否能够登入主机之用。当 /etc/nologin 这个档案存在时，则所有一般使用者均无法再登入系统了！若 /etc/nologin 存在，则一般使用者在登入时， 在他们的终端机上会将该档案的内容显示出来！所以，正常的情况下，这个档案应该是不能存在系统中的。但这个模组对 root 以及已经登入系统中的一般帐号并没有影响。 (注意喔！这与 /etc/nologin.txt 并不相同！)

pam_selinux.so：
SELinux 是个针对程序来进行细部管理权限的功能，SELinux 这玩意儿我们会在第十六章的时候再来详细谈论。由于 SELinux 会影响到使用者执行程式的权限，因此我们利用 PAM 模组，将 SELinux 暂时关闭，等到验证通过后， 再予以启动！

pam_console.so：
当系统出现某些问题，或者是某些时刻你需要使用特殊的终端介面(例如RS232 之类的终端连线设备) 登入主机时， 这个模组可以帮助处理一些档案权限的问题，让使用者可以透过特殊终端介面(console) 顺利的登入系统。

pam_loginuid.so：
我们知道系统帐号与一般帐号的 UID 是不同的！一般帐号 UID 均大于 1000 才合理。因此，为了验证使用者的 UID 真的是我们所需要的数值，可以使用这个模组来进行规范！

pam_env.so：
用来设定环境变数的一个模组，如果你有需要额外的环境变数设定，可以参考 /etc/security/pam_env.conf 这个档案的详细说明。
pam_unix.so：
这是个很复杂且重要的模组，这个模组可以用在验证阶段的认证功能，可以用在授权阶段的帐号授权管理， 可以用在会议阶段的登录档记录等，甚至也可以用在密码更新阶段的检验！非常丰富的功能！这个模组在早期使用得相当频繁喔！

pam_pwquality.so：
可以用来检验密码的强度！包括密码是否在字典中，密码输入几次都失败就断掉此次连线等功能，都是这模组提供的！最早之前其实使用的是pam_cracklib.so 这个模组，后来改成pam_pwquality.so 这个模组，但此模组完全相容于pam_cracklib.so， 同时提供了/etc/security/pwquality.conf 这个档案可以额外指定预设值！比较容易处理修改！

pam_limits.so：
还记得我们在第十章谈到的 ulimit 吗？其实那就是这个模组提供的能力！还有更多细部的设定可以参考： /etc/security/limits.conf 内的说明。