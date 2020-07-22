## 1.基础

1. 正确使用equals方法：

   尽量使用一个常量来.equals(obj)

   ```java
   "SnailClimb".equals(str);// false 
   ```

   更推荐使用java.util.Objects#equals

   ```java
   Objects.equals(null,"SnailClimb");// false
   ```

2. BigDecimal和浮点数

   浮点数之间的等值判断，基本数据类型不能使用"=="，包装数据类型不能使用`equals`来判断。

   浮点数可能存在精度丢失，故使用`BigDecimal`类型

   ```java
   BigDecimal a = new BigDecimal("1.0");//使用字符串作为构造函数的参数可以得到一个完整的1.0，而不是后面有很多位小数
   BigDecimal b = new BigDecimal("0.9");
   BigDecimal c = new BigDecimal("0.8");
   BigDecimal x = a.subtract(b);// 0.1
   BigDecimal y = b.subtract(c);// 0.1
   System.out.println(x.equals(y));// true 
   ```

   ```
   注意：我们在使用BigDecimal时，为了防止精度丢失，推荐使用它的 BigDecimal(String) 构造方法来创建对象。《阿里巴巴Java开发手册》对这部分内容也有提到如下图所示。
   ```

   BigDecimal之间的比较

   `a.compareTo(b)` : 返回 -1 表示小于，0 表示 等于， 1表示 大于。

   ```java
   BigDecimal a = new BigDecimal("1.0");
   BigDecimal b = new BigDecimal("0.9");
   System.out.println(a.compareTo(b));// 1
   ```

   BigDecimal保留小数位数

   通过 `setScale`方法设置保留几位小数以及保留规则。保留规则有挺多种，不需要记，IDEA会提示。

   ```java
   BigDecimal m = new BigDecimal("1.255433");
   BigDecimal n = m.setScale(3,BigDecimal.ROUND_HALF_DOWN);
   System.out.println(n);// 1.255
   ```

3. 1.4. 基本数据类型与包装数据类型的使用标准

   Reference:《阿里巴巴Java开发手册》

   - 【强制】所有的 POJO 类属性必须使用**包装数据类型**。
   - 【强制】RPC (远程调用方法)方法的返回值和参数必须使用包装数据类型。
   - 【推荐】所有的局部变量使用基本数据类型。

   比如我们如果自定义了一个Student类,其中有一个属性是成绩score,如果用Integer而不用int定义,一次考试,学生可能没考,值是null,也可能考了,但考了0分,值是0,这两个表达的状态明显不一样.

   **说明** :POJO 类属性没有初值是提醒使用者在需要使用时，必须自己显式地进行赋值，任何 NPE 问题，或者入库检查，都由使用者来保证。

   **正例** : 数据库的查询结果可能是 null，因为自动拆箱，用基本数据类型接收有 NPE 风险。

   **反例** : 比如显示成交总额涨跌情况，即正负 x%，x 为基本数据类型，调用的 RPC 服务，调用不成功时，返回的是默认值，页面显示为 0%，这是不合理的，应该显示成中划线。所以包装数据类型的 null 值，能够表示额外的信息，如:远程调用失败，异常退出。



## 集合

1. 如何正确使用Collections.toArray()

```java
String [] s= new String[]{
    "dog", "lazy", "a", "over", "jumps", "fox", "brown", "quick", "A"
};
List<String> list = Arrays.asList(s);
Collections.reverse(list);
s=list.toArray(new String[0]);//没有指定类型的话会报错
```

由于JVM优化，`new String[0]`作为`Collection.toArray()`方法的参数现在使用更好，`new String[0]`就是起一个模板的作用，指定了返回数组的类型，0是为了节省空间，因为它只是为了说明返回的类型。

2. 如何将一个数组转化为集合

   ```java
   List list = new ArrayList<>(Arrays.asList("a", "b", "c"))
   ```

3. 不要在foreach循环内add/remove一个元素

   原因：foreach会触发迭代器操作，如果要进行`remove`操作，可以调用迭代器的 `remove`方法而不是集合类的 remove 方法。因为如果列表在任何时间从结构上修改创建迭代器之后，以任何方式除非通过迭代器自身`remove/add`方法，迭代器都将抛出一个`ConcurrentModificationException`,这就是单线程状态下产生的 **fail-fast 机制**。