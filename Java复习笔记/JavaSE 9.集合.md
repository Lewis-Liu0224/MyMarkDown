# 集合

## 集合背景

在Java中，如果一个Java对象可以在内部持有若干其他Java对象，并对外提供访问接口，我们把这种Java对象称为集合，我们需要各种不同类型的集合类来处理不同的数据，因此才有不同的集合类

Collection是除了Map以外所有其他集合类的根接口。

- `List`：一种有序列表的集合，例如，按索引排列的`Student`的`List`；
- `Set`：一种保证没有重复元素的集合，例如，所有无重复名称的`Student`的`Set`；
- `Map`：一种通过键值（key-value）查找的映射表集合，例如，根据`Student`的`name`查找对应`Student`的`Map`。

Java集合的设计较为久远，其中有一些是遗留类，不应该继续使用

- `Hashtable`：一种线程安全的`Map`实现；
- `Vector`：一种线程安全的`List`实现；
- `Stack`：基于`Vector`实现的`LIFO`的栈。

还有一小部分接口是遗留接口，也不应该继续使用：

- `Enumeration`：已被`Iterator`取代。

## List

List和数组功能相近，List比起数组在功能使用上进行了扩展，并且List允许我们添加重复的元素、以及还可以添加null。

在数组中如果要删除或者添加中间的一个元素，需要移动较多位置，十分麻烦，而在`ArrayList`中系统默认会创建比需要的大小空间更大的数组来存储数据（比如一个`ArrayList`拥有5个元素，实际数组大小为`6`（即有一个空位））。

在当`ArrayList`存满时，`ArrayList`会事先创建一个更大的新数组，将旧元素赋值到新数组，所以`ArrayList`把添加和删除的操作封装起来，让我们操作`List`类似于操作数组，却不用关心内部元素如何移动。

### `ArrayList`与`LinkList`的不同

`LinkList`的实现方式不是数组而是链表，两者对比各有好坏：

|                     | `ArrayList`  | `LinkedList`         |
| :------------------ | :----------- | :------------------- |
| 获取指定元素        | 速度很快     | 需要从头开始查找元素 |
| 添加元素到末尾      | 速度很快     | 速度很快             |
| 在指定位置添加/删除 | 需要移动元素 | 不需要移动元素       |
| 内存占用            | 少           | 较大                 |

### 快速实现List

除了使用`ArrayList`和`LinkedList`，我们还可以通过`List`接口提供的`of()`方法，根据给定元素快速创建`List`：

```
List<Integer> list = List.of(1, 2, 5);
```

但是`List.of()`方法不接受`null`值，如果传入`null`，会抛出`NullPointerException`异常。

### 遍历List

利用for语句可以很快遍历`ArrayList`，但是对于`LinkList`，数组越长访问越慢。对此Java提供并且推荐使用`Iterator`迭代器，迭代器会根据List的不同类型，进行不同的迭代方法，并且总是具有最高的访问效率。使用方法如下。

```java
public class Main {
    public static void main(String[] args) {
        List<String> list = List.of("apple", "pear", "banana");
        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            String s = it.next();
            System.out.println(s);
        }
    }
}
```

此外，只要实现了`Iterable`接口的集合类都可以直接使用`for each`循环来遍历，Java编译器本身并不知道如何遍历集合对象，但它会自动把`for each`循环变成`Iterator`的调用

### List与Array的互换：

直接调用List的`toArray()`方法`Integer[] array = list.toArray(new Integer[3]);`

通常指定一个恰好大小的数组`Integer[] array = list.toArray(new Integer[list.size()]);`