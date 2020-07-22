# 注解

## 注解的类型

第一类是由编译器使用的注解，例如：

- `@Override`：让编译器检查该方法是否正确地实现了覆写；
- `@SuppressWarnings`：告诉编译器忽略此处代码产生的警告。

这类注解不会被编译进入`.class`文件，它们在编译后就被编译器扔掉了。

第二类是由工具处理`.class`文件使用的注解，比如有些工具会在加载class的时候，对class做动态修改，实现一些特殊的功能。这类注解会被编译进入`.class`文件，但加载结束后并不会存在于内存中。这类注解只被一些底层库使用，一般我们不必自己处理。

第三类是在程序运行期能够读取的注解，它们在加载后一直存在于JVM中，这也是最常用的注解。例如，一个配置了`@PostConstruct`的方法会在调用构造方法后自动被调用（这是Java代码读取该注解实现的功能，JVM并不会识别该注解）。

## 注解的定义

```java
public @interface Report {
    int type() default 0;
    String level() default "info";
    String value() default "";
}
```

使用`@interface`语法定义注解，通常规定使用default设定一个默认值。并且最常用的参数应当命名为value

## 元注解

一些可以修饰其他注解的注解成为元注解。Java库已经定义了一些元注解，以供开发者使用。

### `@Target`

使用`@Target`可以定义`Annotation`能够被应用于源码的哪些位置：

- 类或接口：`ElementType.TYPE`；
- 字段：`ElementType.FIELD`；
- 方法：`ElementType.METHOD`；
- 构造方法：`ElementType.CONSTRUCTOR`；
- 方法参数：`ElementType.PARAMETER`。

### `@Retention`

另一个重要的元注解`@Retention`定义了`Annotation`的生命周期，若`@Retention`不存在，则该Annotation默认为CLASS：

- 仅编译期：`RetentionPolicy.SOURCE`；
- 仅class文件：`RetentionPolicy.CLASS`；
- 运行期：`RetentionPolicy.RUNTIME`。

## 注解的处理

一般情况下，注解的读取指的是读取RUNTIME类型的注解，其他两种类型在运行期之前早已经被使用完，而RUNTIME类型的注解会被加载进JVM，并且在运行期可以被程序读取。

Java提供的使用反射API读取`Annotation`的方法包括：

判断某个注解是否存在于`Class`、`Field`、`Method`或`Constructor`：

- `Class.isAnnotationPresent(Class)`
- `Field.isAnnotationPresent(Class)`
- `Method.isAnnotationPresent(Class)`
- `Constructor.isAnnotationPresent(Class)`

例如：

```java
// 判断@Report是否存在于Person类:
Person.class.isAnnotationPresent(Report.class);
```

读取注解的方法：

- `Class.getAnnotation(Class)`
- `Field.getAnnotation(Class)`
- `Method.getAnnotation(Class)`
- `Constructor.getAnnotation(Class)`

使用反射API读取`Annotation`有两种方法。方法一是先判断`Annotation`是否存在，如果存在，就直接读取：

```java
Class cls = Person.class;
if (cls.isAnnotationPresent(Report.class)) {
    Report report = cls.getAnnotation(Report.class);
    ...
}
```

第二种方法是直接读取`Annotation`，如果`Annotation`不存在，将返回`null`：

```java
Class cls = Person.class;
Report report = cls.getAnnotation(Report.class);
if (report != null) {
   ...
}
```

读取方法参数的Annotation较为麻烦，一个方法可能有多个参数，而一个参数可能有多个注解。所以要用一个二维数组来表示。`Annotation[][] annos = Method.getParameterAnnotations();`。其中`annos[0]`表示第一个参数的所有注解，同理`annos[1][2]`表示第二个参数的第三个注解。