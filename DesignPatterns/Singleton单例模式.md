### 单例模式Single

#### 饿汉模式：

类加载就创建对象，没有多线程的安全问题。

缺点：耗费系统资源，有可能创建了实例却没有使用。

```java
/**
 * @Author:Lius
 * @Date: 2020/7/22 16:32
 */
public class HungaryMan {

    private final static HungaryMan HUNGARY_MAN = new HungaryMan();

    private HungaryMan(){}

    public static HungaryMan getInstance(){
        return HUNGARY_MAN;
    }
}
```

#### 懒汉模式（线程不安全）

当在使用时才去创建实例，有可能发生线程问题。

```java
/**
 * @Author:Lius
 * @Date: 2020/7/22 16:36
 */
public class LazyManNotSafe {

    private static LazyManNotSafe LAZY_MAN_NOT_SAFE;

    private LazyManNotSafe(){
        System.out.println(Thread.currentThread().getName());
    }

    public static LazyManNotSafe getLazyManNotSafe() {
        if (LAZY_MAN_NOT_SAFE==null){
            LAZY_MAN_NOT_SAFE = new LazyManNotSafe();
        }
        return LAZY_MAN_NOT_SAFE;
    }
}
```

#### 懒汉模式（线程安全）

当在使用时才去创建实例，使用DCL（double check lock）。

```java
/**
 * @Author:Lius
 * @Date: 2020/7/22 16:42
 */
public class LazyManSafe {

    private static LazyManSafe LAZY_MAN_SAFE;

    private LazyManSafe(){
        System.out.println(Thread.currentThread().getName());
    }

    public static LazyManSafe getLazyManSafe() {
        if (LAZY_MAN_SAFE==null){
            synchronized (LazyManSafe.class){
                if (LAZY_MAN_SAFE==null) {
                    LAZY_MAN_SAFE = new LazyManSafe();
                }
            }
        }
        return LAZY_MAN_SAFE;
    }
}
```

