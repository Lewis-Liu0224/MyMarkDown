# Set和Queue

## Set

Map是用于存储key-value的映射，对于key的对象，是不能重复的。并且充当key的对象，是不能重复的，并且不但需要正确覆写equals()方法，还要正确覆写hashCode()方法。

如果只需要key而不需要value，那么就可以使用Set。Set用于存储不重复的元素集合，我们经常用Set用于去除重复元素，它提供一下方法：

- 将元素添加进`Set`：`boolean add(E e)`
- 将元素从`Set`删除：`boolean remove(Object e)`
- 判断是否包含元素：`boolean contains(Object e)`

![image-20200426095651228](E:\homework\Markdown\img\image-20200426095651228.png)

通常我们根据情境分别使用HashSet和TreeSet。

## Queue

队列也是一种经常使用的集合。Queue实际上是实现了一种先进先出（FIFO：First in First Out）的有序表。它和List的区别在于。List可以在任意位置添加和删除元素。而Queue只有两个操作：

1. 把元素添加到队列末尾

2. 从队列头部取出元素(可以选择删除取出和不删除取出)

在Java标准库中，队列接口Queue定义了以下几个方法：

- `int size()`：获取队列长度；
- `boolean add(E)`/`boolean offer(E)`：添加元素到队尾；
- `E remove()`/`E poll()`：获取队首元素并从队列中删除；
- `E element()`/`E peek()`：获取队首元素但并不从队列中删除。

关于添加、删除操作总是有两个方法，在操作失败时，这两种方法对应的失败处理行为是不同的。

|                    | throw Exception | 返回false或null    |
| :----------------- | :-------------- | :----------------- |
| 添加元素到队尾     | add(E e)        | boolean offer(E e) |
| 取队首元素并删除   | E remove()      | E poll()           |
| 取队首元素但不删除 | E element()     | E peek()           |

## PriorityQueue

Queue是一个FIFO的队列，而PriorityQueue与Queue的区别是它的出队顺序与元素的优先级有关，对PriorityQueue调用remove（）和poll（）方法，返回的总是优先级最高的元素。

要实现优先级排序，则必须实现Comparable接口，若放入的元素的类没有实现Comparable接口，PriorityQueue允许我们提供一个Comparator对象来判断两个元素的顺序。

```java
Queue<User> q = new PriorityQueue<>(new UserComparator());

class UserComparator implements Comparator<User> {
    public int compare(User u1, User u2) {
        if (u1.number.charAt(0) == u2.number.charAt(0)) {
            // 如果两人的号都是A开头或者都是V开头,比较号的大小:
            return u1.number.compareTo(u2.number);
        }
        if (u1.number.charAt(0) == 'V') {
            // u1的号码是V开头,优先级高:
            return -1;
        } else {
            return 1;
        }
    }
}

```

## Deque

Queue是队列，使用的是FIFO，而Deque(Double Ended Queue)是双端队列，允许两头都进，两头都出。

|                    |         Queue          |              Deque              |
| :----------------: | :--------------------: | :-----------------------------: |
|   添加元素到队尾   | add(E e) / offer(E e)  |  addLast(E e) / offerLast(E e)  |
|  取队首元素并删除  | E remove() / E poll()  | E removeFirst() / E pollFirst() |
| 取队首元素但不删除 | E element() / E peek() |  E getFirst() / E peekFirst()   |
|   添加元素到队首   |           无           | addFirst(E e) / offerFirst(E e) |
|  取队尾元素并删除  |           无           |  E removeLast() / E pollLast()  |
| 取队尾元素但不删除 |           无           |   E getLast() / E peekLast()    |

## Collections工具类

### 创建空集合

- 创建空List：`List emptyList()`
- 创建空Map：`Map emptyMap()`
- 创建空Set：`Set emptySet()`

返回的空集合是不可变集合，无法向其中添加或删除元素。

### 创建单元素集合

`Collections`提供了一系列方法来创建一个单元素集合：

- 创建一个元素的List：`List singletonList(T o)`
- 创建一个元素的Map：`Map singletonMap(K key, V value)`
- 创建一个元素的Set：`Set singleton(T o)`

要注意到返回的单元素集合也是不可变集合，无法向其中添加或删除元素。

### 使用List.of创建集合

实际上，使用`List.of(T...)`更方便，因为它既可以创建空集合，也可以创建单元素集合，还可以创建任意个元素的集合：

```java
List<String> list1 = List.of(); // empty list
List<String> list2 = List.of("apple"); // 1 element
List<String> list3 = List.of("apple", "pear"); // 2 elements
List<String> list4 = List.of("apple", "pear", "orange"); // 3 elements
```

### 排序

`Collections.sort(list);`注意必须传入可变的List

### 洗牌

`Collections.shuffle(list);`

### 转换为不可变集合

`Collections`还提供了一组方法把可变集合封装成不可变集合：

- 封装成不可变List：`List unmodifiableList(List list)`
- 封装成不可变Set：`Set unmodifiableSet(Set set)`
- 封装成不可变Map：`Map unmodifiableMap(Map m)`

这种封装实际上是通过创建一个代理对象，拦截掉所有修改方法实现的。我们来看看效果：

```java
public class Main {
    public static void main(String[] args) {
        List<String> mutable = new ArrayList<>();
        mutable.add("apple");
        mutable.add("pear");
        // 变为不可变集合:
        List<String> immutable = Collections.unmodifiableList(mutable);
        immutable.add("orange"); // UnsupportedOperationException!
    }
}
```

但对原始的mutable进行增删是可以的，并且会直接影响到后面的'不可变'List，所以在不可变转换操作完后，要扔掉原来的可变集合：`mutable = null`。

### 线程安全集合

`Collections`还提供了一组方法，可以把线程不安全的集合变为线程安全的集合：

- 变为线程安全的List：`List synchronizedList(List list)`
- 变为线程安全的Set：`Set synchronizedSet(Set s)`
- 变为线程安全的Map：`Map synchronizedMap(Map m)`

多线程的概念我们会在后面讲。因为从Java 5开始，引入了更高效的并发集合类，所以上述这几个同步方法已经没有什么用了。