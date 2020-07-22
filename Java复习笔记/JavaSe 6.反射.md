# 反射

## 反射的背景

除了基本类型之外，Java的其他类型都是class。

当在第一次读取到该class时，将其加载至内存，每次加载一个class，JVM就为其创建一个Class实例并将其关联。以String类型为例，当使用String类型时，JVM将会加载String类，读取`String.class`到内存，并创建一个Class实例与String关联，如`Class cls = new Class(String);`。

并且Class的构造方法是private，只有JVM能创建。

JVM的每个Class实例都指向一个数据类型（或interface，如Runnable）

![image-20200424094750568](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200424094750568.png)

该Class通常包含该类的完整信息



![image-20200424094930706](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200424094930706.png)

这种通过`Class`实例获取`class`信息的方法称为反射（Reflection）。

## 通过Class实例获取class信息的方法

方法一：直接通过一个`class`的静态变量`class`获取：

```java
Class cls = String.class;
```

方法二：如果我们有一个实例变量，可以通过该实例变量提供的`getClass()`方法获取：

```java
String s = "Hello";
Class cls = s.getClass();
```

方法三：如果知道一个`class`的完整类名，可以通过静态方法`Class.forName()`获取：

```java
Class cls = Class.forName("java.lang.String");
```

## 反射的一些注意事项

注意到数组（例如`String[]`）也是一种`Class`，而且不同于`String.class`，它的类名是`[Ljava.lang.String`。此外，JVM为每一种基本类型如`int`也创建了`Class`，通过`int.class`访问。

JVM加载类的方式为动态加载，当第一次需要用到该class时，才将该类加载至内存

# 获取字段信息

在获取该类的Class实例后，便可以通过一些方法获取该类的一些信息字段，方法如下

- Field `getField(name)`：根据字段名获取某个public的field（包括父类）
- Field `getDeclaredField(name)`：根据字段名获取当前类的某个field（不包括父类）
- Field[] `getFields()`：获取所有public的field（包括父类）
- Field[] `getDeclaredFields()`：获取当前类的所有field（不包括父类）

```java
		Person per = new Person("hahaha",12);
        Student student = new Student(per,100);
        Class c = student.getClass();
        Field[] fields = c.getFields();
        for (Field field:fields) {
            System.out.println(field);
        }
```

获取到Field之后，Field还有一些特定的获取信息 方法例如：

```java
Field f = String.class.getDeclaredField("value");
f.getName(); // "value"
f.getType(); // class [B 表示byte[]类型
int m = f.getModifiers();
Modifier.isFinal(m); // true
Modifier.isPublic(m); // false
Modifier.isProtected(m); // false
Modifier.isPrivate(m); // true
Modifier.isStatic(m); // false
```

## 通过反射获取实例的字段值

通过Field这个实例，我们还可以获取某一个特定实例的字段值例如：

```java
		Person per = new Person("hahaha",12);
        Student student = new Student(per,100);
        Class c = student.getClass();
        Field score = c.getDeclaredField("score");
        score.setAccessible(true);//当访问private字段时，必须添加，否则报错：IllegalAccessException
        Object i = score.get(student);
        System.out.println((int)i);//100
```

在这里我们获取了student这个实例的score字段的值,当访问private字段时，必须添加`Field.setAccessible(true);`否则报错：`IllegalAccessException`

## 通过反射设置实例的字段值

```java
        Person per = new Person("hahaha",12);
        Student student = new Student(per,100);
        Class c = student.getClass();
        Field score = c.getDeclaredField("score");
        score.setAccessible(true);
        score.set(student,98);
        System.out.println(student.getScore());//98
```

使用上述两种种方式可以直接查看和修改private字段，它更多地是给工具或者底层框架来使用。此外，`setAccessible(true)`可能会失败。如果JVM运行期存在`SecurityManager`，那么它会根据规则进行检查，有可能阻止`setAccessible(true)`。例如，某个`SecurityManager`可能不允许对`java`和`javax`开头的`package`的类调用`setAccessible(true)`，这样可以保证JVM核心库的安全。

# 获取调用方法

此外还可以通过Class实例获取方法的信息：

- `Method getMethod(name, Class...)`：获取某个`public`的`Method`（包括父类）
- `Method getDeclaredMethod(name, Class...)`：获取当前类的某个`Method`（不包括父类）
- `Method[] getMethods()`：获取所有`public`的`Method`（包括父类）
- `Method[] getDeclaredMethods()`：获取当前类的所有`Method`（不包括父类）

```java
        Person per = new Person("hahaha",12);
        Student student = new Student(per,100);
        Class c = student.getClass();
        Method getPerson = c.getMethod("getPerson");
        System.out.println(getPerson);//public Person Student.getPerson()
```

和Field一样Method也有一些获取信息的方法

- `getName()`：返回方法名称，例如：`"getScore"`；
- `getReturnType()`：返回方法返回值类型，也是一个Class实例，例如：`String.class`；
- `getParameterTypes()`：返回方法的参数类型，是一个Class数组，例如：`{String.class, int.class}`；
- `getModifiers()`：返回方法的修饰符，它是一个`int`，不同的bit表示不同的含义。

## 通过Method调用方法

```java
        String s = "Hello World";
        Class cls = s.getClass();
        Method substring = cls.getMethod("substring", int.class, int.class);
        String r = (String)substring.invoke(s,0,4);
        System.out.println(r);
```

注意invoke方法必须与之前`getMethod`里的参数一致

调用静态方法时，只需要将invoke的第一个参数设置为null即可

和Field类似，对于非public方法，我们虽然可以通过`Class.getDeclaredMethod()`获取该方法实例，但直接对其调用将得到一个`IllegalAccessException`。为了调用非public方法，我们通过`Method.setAccessible(true)`允许其调用：

# 获取构造方法

`Person p = Person.class.newInstance();`通过该方法可以创建Person实例，但缺点是只能调用Person的空参数构造方法，为此反射提供了其他获取其他构造方法的API：

```java
public class Main {
    public static void main(String[] args) throws Exception {
        // 获取构造方法Integer(int):
        Constructor cons1 = Integer.class.getConstructor(int.class);
        // 调用构造方法:
        Integer n1 = (Integer) cons1.newInstance(123);
        System.out.println(n1);

        // 获取构造方法Integer(String)
        Constructor cons2 = Integer.class.getConstructor(String.class);
        Integer n2 = (Integer) cons2.newInstance("456");
        System.out.println(n2);
    }
}
```

获取构造的方法和之前的Method类似，括号内的参数为所选择的构造方法的参数

- `getConstructor(Class...)`：获取某个`public`的`Constructor`；
- `getDeclaredConstructor(Class...)`：获取某个`Constructor`；
- `getConstructors()`：获取所有`public`的`Constructor`；
- `getDeclaredConstructors()`：获取所有`Constructor`。

注意，需要通过设置`setAccessible(true)`来访问非`public`构造方法。



## 获取类的父类、接口

- `Class getSuperclass()`：获取父类类型；
- `Class[] getInterfaces()`：获取当前类实现的所有接口。

```java
        Class s = Integer.class;
        Class[] is = s.getInterfaces();
        for (Class i : is) {
            System.out.println(i);
```

`interface java.lang.Comparable
interface java.lang.constant.Constable
interface java.lang.constant.ConstantDesc`

判断是否能够向上转型的方法：

如果是两个`Class`实例，要判断一个向上转型是否成立，可以调用`isAssignableFrom()`：

```java
// Integer i = ?
Integer.class.isAssignableFrom(Integer.class); // true，因为Integer可以赋值给Integer
// Number n = ?
Number.class.isAssignableFrom(Integer.class); // true，因为Integer可以赋值给Number
// Object o = ?
Object.class.isAssignableFrom(Integer.class); // true，因为Integer可以赋值给Object
// Integer i = ?
Integer.class.isAssignableFrom(Number.class); // false，因为Number不能赋值给Integer
```

# 动态代理

不编写实现类，直接在运行期创建某个`interface`的实例，例如下面代码

```java
public class Main {
    public static void main(String[] args) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method);
                if (method.getName().equals("morning")) {
                    System.out.println("Good morning, " + args[0]);
                }
                return null;
            }
        };
        Hello hello = (Hello) Proxy.newProxyInstance(
            Hello.class.getClassLoader(), // 传入ClassLoader
            new Class[] { Hello.class }, // 传入要实现的接口
            handler); // 传入处理调用方法的InvocationHandler
        hello.morning("Bob");
    }
}

interface Hello {
    void morning(String name);
}
```

在运行期动态创建一个`interface`实例的方法如下：

1. 定义一个`InvocationHandler`实例，它负责实现接口的方法调用；
2. 通过`Proxy.newProxyInstance()`创建`interface`实例，它需要3个参数：
   1. 使用的`ClassLoader`，通常就是接口类的`ClassLoader`；
   2. 需要实现的接口数组，至少需要传入一个接口进去；
   3. 用来处理接口方法调用的`InvocationHandler`实例。
3. 将返回的`Object`强制转型为接口。