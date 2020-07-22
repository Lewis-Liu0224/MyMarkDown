## Java中不用Stack的原因

在官方的的stack文档中，推荐使用Deque这种方式来实现栈：

```
Deque<Integer> stack = new Deque<Integer>();
```

### 原因

Java 中的 Stack 类，最大的问题是，继承了 Vector 这个动态数组类。Vector和ArrayList一样作为动态数组，有能力在任何地方添加和删除元素，这样就破坏了栈的数据结构。

这样即违背了封装的意义，没有屏蔽掉用户不必要的操作，成为了软件工程中一些bug的来源



## 问题

Java 中的 Stack 实现，实际上，它犯了面向对象设计领域的一个基本错误：Stack 和 Vector 之间的关系，不应该是继承关系，而应该是组合关系（composition）。

1. 继承关系`(is-a)`：即猫是一个动物，程序员是一个雇员
2. 组合关系(`has-a`)：即电脑拥有CPU、内存、显卡



