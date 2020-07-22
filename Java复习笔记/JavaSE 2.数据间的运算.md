# 数据间的运算
## 整数运算
1. 整除运算：运算符号：‘/’，当结果不为一个整数时，丢弃小数位的数据，注意1.除数不能为0否则会报错。2.整除运算为带符号的运算3.注意如果运算结果的大小超过了结果的数据类型范围会发生溢出，运算结果不正确，故运算之前估计好结果的范围
2. 取余运算：运算符号：'%'
```java
		System.out.println(4%3);
        System.out.println((-4%3));
        System.out.println(4%-3);
        System.out.println((-4%-3));
```
结果如下：
1
-1
1
-1
注意：1.取余运算结果不超过%运算符后面的值2.运算结果的符号由被余的数值符号决定

3. 进制间的转换：

   其他进制转十进制：

   ```java
   int x = Integer.parseInt("100",16);//十六进制转换为十进制，第一个参数为需要转换的字符串数字
   int y = Integer.parseInt("100",8);//八进制转换为十进制
   ```

   十进制转换为其他进制：

   ```java
   int x = Integer.toString(100,24);//十进制转24进制
   int y = Integer.toHexString(100);//十进制转换为十六进制
   int z = Integer.toOctalString(100);//十进制转八进制
   int k = Integer.toBinaryString(100);//十进制转二进制
   ```

   

## 布尔运算与关系运算符
关系运算符优先级
:!
: >，>=，<，<=
: ==，!=
: &&
: ||

注意：1.‘&’符号与‘&&’区别在于：‘&&’符号具有短路运算，而‘&’符号不具有短路运算例如：
```java
		//‘||’和‘|’符号同理
 		int a=0;
        if(!(false && a++>0)){
            System.out.println(a);
        }
        a=0;
        if(!(false & a++>0)){
            System.out.println(a);
        }
```
结果为：
0
1