# 泛型

泛型是一种类似”模板代码“的技术，不同语言的泛型实现方式不一定相同。

Java语言的泛型实现方式是擦拭法（Type Erasure）。

所谓擦拭法是指，虚拟机对泛型其实一无所知，所有的工作都是编译器做的。

编译器所看到的代码：

```java
public class Pair<T> {
    private T first;
    private T last;
    public Pair(T first, T last) {
        this.first = first;
        this.last = last;
    }
    
	Pair<String> p = new Pair<>("Hello", "world");
	String first = p.getFirst();
	String last = p.getLast();
```

而经过编译器，JVM所执行的代码为：

```java
public class Pair {
    private Object first;
    private Object last;
    public Pair(Object first, Object last) {
        this.first = first;
        this.last = last;
    }
    
    
	Pair p = new Pair("Hello", "world");
	String first = (String) p.getFirst();
	String last = (String) p.getLast();
```

总结如下：

- 编译器把类型`<T>`视为`Object`；
- 编译器根据`<T>`实现安全的强制转型。

根据擦拭法的一些特性，导致了泛型的一些局限：

1. `<T>`不能是基本类型，Object无法持有基本类型

2. 无法获取带泛型的class，对`Pair<String>`和`Pair<Integer>`类获取class时，获取到的是同一个Class，也就是Pair的Class。

3. 不能实例化T类型,

   ```java
   public class Pair<T> {
       private T first;
       private T last;
       public Pair() {
           // Compile error:
           first = new T();//等于first = new Object()
           last = new T();//等于last = new Object()
       }
   }
   ```

   如要实例化T类型，必须借助额外的Class<T>参数，注意Integer不能使用，因为Integer没有默认的构造器，无法实例化：

   ```java
   public class Pair<T> {
       private T first;
       private T last;
       public Pair(Class<T> clazz) {
           first = clazz.newInstance();
           last = clazz.newInstance();
       }
   }
   ```

   在通常情况下，我们无法获得某个类的泛型T，但是在父类是泛型的情况下，子类可以获取父类的泛型类型`public class IntPair extends Pair<Integer>`，获取父类泛型的代码较为复杂：

   ```java
   public class Main {
       public static void main(String[] args) {
           Class<IntPair> clazz = IntPair.class;
           Type t = clazz.getGenericSuperclass();
           if (t instanceof ParameterizedType) {
               ParameterizedType pt = (ParameterizedType) t;
               Type[] types = pt.getActualTypeArguments(); // 可能有多个泛型类型
               Type firstType = types[0]; // 取第一个泛型类型
               Class<?> typeClass = (Class<?>) firstType;
               System.out.println(typeClass); // Integer
           }
   
       }
   }
   
   class Pair<T> {
       private T first;
       private T last;
       public Pair(T first, T last) {
           this.first = first;
           this.last = last;
       }
       public T getFirst() {
           return first;
       }
       public T getLast() {
           return last;
       }
   }
   
   class IntPair extends Pair<Integer> {
       public IntPair(Integer first, Integer last) {
           super(first, last);
       }
   }
   ```


## extends通配符

在泛型中`Pair<Integer>`并不是`Pair<Number>`的子类，如果我们定义了一个方法，指明它的接收参数类型是`Pair<Number>`。

`int sum = PairHelper.add(new Pair<Number>(1, 2));`该代码可以正常编译，即使传入的实际参数是Integer。

但如果写成`int sum = PairHelper.add(new Pair<Integer>(1, 2));`则会报错。

此时我们可以使用`extends`，具体代码为：`static int add(Pair<? extends Number> p)`这样只要属于Number的子类都可以作为参数传入

使用`extends`通配符表示可以读，不能写。

## super通配符

和`extends`通配符相反，这次，我们希望接受`Pair<Integer>`类型，以及`Pair<Number>`、`Pair<Object>`，因为`Number`和`Object`是`Integer`的父类，`setFirst(Number)`和`setFirst(Object)`实际上允许接受`Integer`类型。

我们使用`super`通配符来改写这个方法：

```java
void set(Pair<? super Integer> p, Integer first, Integer last) {
    p.setFirst(first);
    p.setLast(last);
}
```

因此，使用`<? super Integer`通配符表示：

- 允许调用`set(? super Integer)`方法传入`Integer`的引用；
- 不允许调用`get()`方法获得`Integer`的引用，除了获取Object的引用是例外。

## 对比extends和super通配符

我们再回顾一下`extends`通配符。作为方法参数，`<? extends T>`类型和`<? super T>`类型的区别在于：

- <? extends T>允许调用读方法`T get()`获取`T`的引用，但不允许调用写方法`set(T)`传入`T`的引用（传入`null`除外）；
- `<? super T>`允许调用写方法`set(T)`传入`T`的引用，但不允许调用读方法`T get()`获取`T`的引用（获取`Object`除外）。

一个是允许读不允许写，另一个是允许写不允许读。

先记住上面的结论，我们来看Java标准库的`Collections`类定义的`copy()`方法：

```
public class Collections {
    // 把src的每个元素复制到dest中:
    public static <T> void copy(List<? super T> dest, List<? extends T> src) {
        for (int i=0; i<src.size(); i++) {
            T t = src.get(i);
            dest.add(t);
        }
    }
}
```

它的作用是把一个`List`的每个元素依次添加到另一个`List`中。它的第一个参数是`List`，表示目标`List`，第二个参数`List`，表示要复制的`List`。我们可以简单地用`for`循环实现复制。在`for`循环中，我们可以看到，对于类型``的变量`src`，我们可以安全地获取类型`T`的引用，而对于类型``的变量`dest`，我们可以安全地传入`T`的引用。

这个`copy()`方法的定义就完美地展示了`extends`和`super`的意图：

- `copy()`方法内部不会读取`dest`，因为不能调用`dest.get()`来获取`T`的引用；
- `copy()`方法内部也不会修改`src`，因为不能调用`src.add(T)`。

这是由编译器检查来实现的。如果在方法代码中意外修改了`src`，或者意外读取了`dest`，就会导致一个编译错误：

```
public class Collections {
    // 把src的每个元素复制到dest中:
    public static <T> void copy(List<? super T> dest, List<? extends T> src) {
        ...
        T t = dest.get(0); // compile error!
        src.add(t); // compile error!
    }
}
```

这个`copy()`方法的另一个好处是可以安全地把一个`List`添加到`List`，但是无法反过来添加：

```
// copy List<Integer> to List<Number> ok:
List<Number> numList = ...;
List<Integer> intList = ...;
Collections.copy(numList, intList);

// ERROR: cannot copy List<Number> to List<Integer>:
Collections.copy(intList, numList);
```

而这些都是通过`super`和`extends`通配符，并由编译器强制检查来实现的。

## PECS原则

何时使用`extends`，何时使用`super`？为了便于记忆，我们可以用PECS原则：Producer Extends Consumer Super。

即：如果需要返回`T`，它是生产者（Producer），要使用`extends`通配符；如果需要写入`T`，它是消费者（Consumer），要使用`super`通配符。

还是以`Collections`的`copy()`方法为例：

```
public class Collections {
    public static <T> void copy(List<? super T> dest, List<? extends T> src) {
        for (int i=0; i<src.size(); i++) {
            T t = src.get(i); // src是producer
            dest.add(t); // dest是consumer
        }
    }
}
```

需要返回`T`的`src`是生产者，因此声明为`List`，需要写入`T`的`dest`是消费者，因此声明为`List`。

## 无限定通配符

Java的泛型还允许使用无限定通配符（Unbounded Wildcard Type），即只定义一个`?`：

因为``通配符既没有`extends`，也没有`super`，因此：

- 不允许调用`set(T)`方法并传入引用（`null`除外）；
- 不允许调用`T get()`方法并获取`T`引用（只能获取`Object`引用）。

换句话说，既不能读，也不能写，那只能做一些`null`判断：

```java
static boolean isNull(Pair<?> p) {
    return p.getFirst() == null || p.getLast() == null;
}
```

大多数情况下，可以引入泛型参数``消除``通配符：

```java
static <T> boolean isNull(Pair<T> p) {
    return p.getFirst() == null || p.getLast() == null;
}
```

``通配符有一个独特的特点，就是：`Pair`是所有`Pair`的超类：

```java
public class Main {
 public static void main(String[] args) {
        Pair<Integer> p = new Pair<>(123, 456);
        Pair<?> p2 = p; // 安全地向上转型
        System.out.println(p2.getFirst() + ", " + p2.getLast());
    }
}
```