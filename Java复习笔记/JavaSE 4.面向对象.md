# 面向对象和类

## 方法重载：

在下面这个类中hello方法可以根据参数的不同而调用不同的方法，实现方法的重载

```java
class Hello {
    public void hello() {
        System.out.println("Hello, world!");
    }

    public void hello(String name) {
        System.out.println("Hello, " + name + "!");
    }

    public void hello(String name, int age) {
        if (age < 18) {
            System.out.println("Hi, " + name + "!");
        } else {
            System.out.println("Hello, " + name + "!");
        }
    }
}
```

## 继承

Person类有两个成员变量Name和Age，Student有三个成员变量Name、Age和Score，这时Student其实是属于Person的关系，故可以让`class Student extends Person`这时Student类只需要写Score这个变量以及成员方法，其他变量均由Person继承而来。

注意：1.子类无法直接调用父类的private属性（protected和public可以）2.子类构造方法要加上super()发放否则会报错。3.一般允许向上转型（即Student类可向Person类转换）

## 多态

当子类定义了一个和父类一样的方法（方法名相同、方法参数相同、返回值相同），则被称为覆写，往往在该方法上添加标记@Override以让编译器帮助检查是否进行了正确的覆写，但也可以不加。

而多态是指，针对某个类型的方法调用，其真正执行的方法取决于运行时期实际类型的方法，例：

```java
Person p = new Student();
p.run(); // 运行的为Student中的run方法
```

## 抽象方法和抽象类

当一个类中包含抽象方法，则这个类为抽象类（该类也可以包含非抽象方法），并且抽象类无法实例化，只能用作被继承

`抽象类：abstract class 抽象方法：public abstract void run()`

抽象方法不实现任何功能，也无法被调用，只是用来被子类去覆写它。

## 接口

如果抽象类中都为抽象方法，则可以将该类写作接口，以省略`public abstract`，当继承该类时使用implements，并且可以继承多个接口。

## 静态方法和静态变量

静态变量为该类所有实例共同享有的，往往通过`类名.静态变量名`来调用。同理，静态方法不需要实例化一个对象来调用，可直接通过类名.方法名直接调用。

接口可以定义静态字段，但是必须声明final类型

## 包

为了防止类之间的重名，通过package来解决名字冲突。包名之间没有继承关系，包名要尽量避免关键字。

## 枚举类

枚举背景：一般在java中定义常量一般使用static final，在Java 5中为这种类定义了一个特殊的类：`enum`

```java
public class Weekday {
    public static final int SUN = 0;
    public static final int MON = 1;
    public static final int TUE = 2;
    public static final int WED = 3;
    public static final int THU = 4;
    public static final int FRI = 5;
    public static final int SAT = 6;
}
```

`enum`的用法

```java
//一般定义方法：
public enum Color {
     
     RED, GREEN, BLANK, YELLOW 
 
}
//一般与switch搭配
public class B {
    public static void main(String[] args) {
        showColor( Color.RED );
    }
    static void showColor(Color color){
        switch ( color ) {
        case BLANK:
            System.out.println( color );
            break;
        case RED :
            System.out.println( color );
            break;
        default:
            System.out.println( color );
            break;
        }    
    }
}
//可添加自定义函数
public enum Color {
     
     RED("红色", 1), GREEN("绿色", 2), BLANK("白色", 3), YELLO("黄色", 4);
     
     
    private String name ;
    private int index ;
     
    private Color( String name , int index ){
        this.name = name ;
        this.index = index ;
    }
     
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
     
 
}
```

## 记录类（不变类）

用final修饰类即为记录类，特点有1.无法派生子类2.无法修改该类的字段

从Java 14开始，引入了新的`Record`类。我们定义`Record`类时，使用关键字`record`。把上述`Point`类改写为`Record`类，代码如下：`public record Point(int x, int y) {}`

经过编译最终生成的如下：

```java
public final class Point extends Record {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public String toString() {
        return String.format("Point[x=%s, y=%s]", x, y);
    }

    public boolean equals(Object o) {
        ...
    }
    public int hashCode() {
        ...
    }
```

## `BigInteger和BigDecimal`类

当java原生的long数据类型不满足需要时，可以使用`BigInteger`类。`BigInteger`类内部用的是一个`int[]`数组来模拟一个大整数。`BigInteger`没有范围，但运算速度较慢。而`BigDecimal`表示的是一个浮点数

## Random类

Random类用来创建伪随机数，当`new random（long seed）`内的种子值相同时，所生成的随机数往往相同，不声明时，系统默认把当前的时间戳当做种子。

Random类的方法：

```java
Random r = new Random();
r.nextInt(); // 2071575453,每次都不一样
r.nextInt(10); // 5,生成一个[0,10)之间的int
r.nextLong(); // 8811649292570369305,每次都不一样
r.nextFloat(); // 0.54335...生成一个[0,1)之间的float
r.nextDouble(); // 0.3716...生成一个[0,1)之间的double
```

`SecureRandom`安全随机数类，此类无法指定种子，用法和Random类大同

## 类的equals方法编写

编写类的`equals()`方法必须满足以下条件：

- 自反性（Reflexive）：对于非`null`的`x`来说，`x.equals(x)`必须返回`true`；
- 对称性（Symmetric）：对于非`null`的`x`和`y`来说，如果`x.equals(y)`为`true`，则`y.equals(x)`也必须为`true`；
- 传递性（Transitive）：对于非`null`的`x`、`y`和`z`来说，如果`x.equals(y)`为`true`，`y.equals(z)`也为`true`，那么`x.equals(z)`也必须为`true`；
- 一致性（Consistent）：对于非`null`的`x`和`y`来说，只要`x`和`y`状态不变，则`x.equals(y)`总是一致地返回`true`或者`false`；
- 对`null`的比较：即`x.equals(null)`永远返回`false`。

具体到方法内实现`equal()`还是比较简单的以Person为例：

```java
public class Person {
    public String name;
    public int age;
}


public boolean equals(Object o) {
    if (o instanceof Person) {
        Person p = (Person) o;
        return Objects.equals(this.name, p.name) && this.age == p.age;
    }
    return false;
}
```

因此，我们总结一下`equals()`方法的正确编写方法：

1. 先确定实例“相等”的逻辑，即哪些字段相等，就认为实例相等；
2. 用`instanceof`判断传入的待比较的`Object`是不是当前类型，如果是，继续比较，否则，返回`false`；
3. 对引用类型用`Objects.equals()`比较，对基本类型直接用`==`比较。

使用`Objects.equals()`比较两个引用类型是否相等的目的是省去了判断`null`的麻烦。两个引用类型都是`null`时它们也是相等的。