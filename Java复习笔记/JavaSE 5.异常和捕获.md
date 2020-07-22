# 异常和捕获

## 异常

![image-20200420100900504](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200420100900504.png)

Java中异常为类的关系，在不愿意使用try..catch的时候，可以选择在该方法名的后面添加throws加上异常的类，这样做可以省去try..catch但是如果出现异常程序会直接退出。当可能出现异常时，不能即不捕获又不抛出，如果这样做编译器将会报错。

- 必须捕获的异常，包括`Exception`及其子类，但不包括`RuntimeException`及其子类，这种类型的异常称为Checked Exception。
- 不需要捕获的异常，包括`Error`及其子类，`RuntimeException`及其子类。

### try...catch

在catch的过程中，要注意顺序，子类往往放在上面（就近原则），否则该异常将永远捕获不到，如下例。

```java
public static void main(String[] args) {
    try {
        process1();
        process2();
        process3();
    } catch (IOException e) {
        System.out.println("IO error");
    } catch (UnsupportedEncodingException e) { // 永远捕获不到
        System.out.println("Bad encoding");
    }
}
```

### finally语句

当期望无论有没有发生异常都要最后执行一些代码时，通常使用finally(如一些清理、提示工作)，finally的特点有1.finally总是最后执行的。2.finally是可以不必须写的

### 正确的捕获根本原因的异常

错误做法：

```java
public class Main {
    public static void main(String[] args) {
        try {
            process1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void process1() {
        try {
            process2();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }
    }
    static void process2() {
        throw new NullPointerException();
    }
}
```

这样打印的错误栈为：

```java
java.lang.IllegalArgumentException
    at Main.process1(Main.java:15)
    at Main.main(Main.java:5)
```

要追踪到完整的异常栈，需要在构造异常的时候将原始的异常当做参数传进去，以保持原始异常，定位案发现场

```java
public class Main {
    public static void main(String[] args) {
        try {
            process1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void process1() {
        try {
            process2();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);//当做参数传进去
        }
    }
    static void process2() {
        throw new NullPointerException();
    }
}
```

异常如下：

```java
java.lang.IllegalArgumentException: java.lang.NullPointerException
    at Main.process1(Main.java:15)
    at Main.main(Main.java:5)
Caused by: java.lang.NullPointerException
    at Main.process2(Main.java:20)
    at Main.process1(Main.java:13)
```

