# 关于字符和字符串

## 一、char

### char类型的声明方式和性质：

```java
		char a = '中';
        System.out.println(a);
        //result:'中'

        int b = 20013;
        System.out.println((char)b);
        //result:中

        String temp = Integer.toHexString(20013);
        //4e2d
        System.out.println('\u4e2d');
        //result:中
```

1. 将字符放在单引号中
2. 声明一个整型，数值位字符的Unicode值，再强转为char型
3. 使用'\u'后跟字符的Unicode值

### 字符数组的声明方式：

1. `char[] chars = new char[length];//建议此种`
2. `char chars[] = new char[length];`
3. `char[] chars = new char[]{'A','B','C'};`

## 二、String

### String类型的声明方式以及区别：

1. `String s = "abc"`

   该方式为直接在常量池中创建并使用

2. `String s = new String("abc")`

   该方式为在堆中创建对象，方法名为引用该对象的地址

关于两种方法的测试：

```java
        String s11 = "abc";
        String s12 = "abc";
        String s13 = "a"+"bc";//编译器确定为常量，直接到常量池中引用
        System.out.println(s11==s12);//true
        System.out.println(s11==s13);//true

        String s21="ab";
        String s22="abc";
        String s23=s21+"c";//编译器不能确定s21为常量（会在堆区创建一个String对象）
        System.out.println(s23==s22);// false

        String s31 = new String("ab");
        String s32 = new String("abc");
        String s33 = s31+"c";
        System.out.println(s32==s33);   //false
        System.out.println(s32=="abc"); //false
        System.out.println(s33=="abc"); //false
```



### 关于字符串与其他类型的转换

1. String转其他类型：

   ```java
   		String s = "1234";
           String s1 = "true";
           int a = Integer.parseInt(s);
           Long b = Long.parseLong(s);
           float c = Float.parseFloat(s);//123.4
           System.out.println(c);
           boolean b1 = Boolean.parseBoolean(s1);//true
           System.out.println(b1);
   ```

2. 其他类型转换为String类型：

   ```java
   		int  a = 123;
           float c= 1234.1f;
           boolean d = false;
           char[] chars = new char[]{'1','2','3','4'};
   
           String s1 = String.valueOf(a);
           String s2 = String.valueOf(c);
           String s3 = String.valueOf(d);
           String s4 = Arrays.toString(chars);
   ```

   常用的String方法：

   1、字符串比较compareTo()、compareToIgnoreCase(),返回值为正即代表大于，0为等于，负数为小于

   2、字符串查找indexOf、lastIndexOf，根据字符返回该字符所在下标位置
   3、返回指定位置字符charAt，根据下标返回字符
   4、字符串替代replace、replaceAll
   5、字符串反转reverse
   6、字符串转变大小写toUpperCase、toLowerCase
   7、去掉首位空格trim
   8、是否包含某字符/字符串contains

   9、获取子字符串substring(),空格也占一个字符

   10、拆分字符串split()根据标记sign返回字符串数

3. 常见的转义字符包括：

   - `\"` 表示字符`"`
   - `\'` 表示字符`'`
   - `\\` 表示字符`\`
   - `\n` 表示换行符
   - `\r` 表示回车符
   - `\t` 表示Tab
   - `\u####` 表示一个Unicode编码的字符

### `StringBuilder`和`StringBuffer`

`StringBuilder`背景：在String操作发生变化时，因为String的特性，往往需要重新创建新的字符串对象，而丢弃原来的对象，这样十分消耗内存和资源。因此在需要大量字符串操作时，我们使用`StringBuilder`，在操作完成后，我们再将其转换为String类型。`StringBuffer`为`StringBuilder`线程安全版，通过同步保证多个线程操作`StringBuffer`是安全的，但是同步会导致执行速度的下降。

### 对字符串进行拼接

1. 使用`StringJoiner`（需要指定字符开头和结尾时）

   ```java
   //不指定开头结尾时
   var sc = new StringJoiner(",");
   for(String name:names){
       sj.add(name);
   }
   String result = sj.toString();
   //需要指定开头和结尾时
   var sc = new StringJoiner(",","start","end");
   for(String name:names){
       sj.add(name);
   }
   String result = sj.toString();
   ```

   

2. 使用`String.join()`（不需要指定开头和结尾字符时）

   ```java
   	String message = String.join("-", "Java", "is", "cool");
   	// message returned is: "Java-is-cool"
   	List<String> strings = new LinkedList<>();
   	strings.add("Java");strings.add("is");
        strings.add("cool");
        String message = String.join(" ", strings);
        //message returned is: "Java is cool"
   
   ```

   