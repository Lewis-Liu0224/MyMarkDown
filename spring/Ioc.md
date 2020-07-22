### Spring



#### @RestController和@Controller

单独使用@Controller不加@ResponseBody会返回一个视图，对于前后端不分离的情况。

![img](E:\homework\Markdown\spring\img\SpringMVC传统工作流程.png)

@RestController只返回对象，对象的数据以JSON或XML形式写入HTTP响应中

![img](E:\homework\Markdown\spring\img\SpringMVCRestController.png)

#### IOC

控制反转（Inverse of Control）是一种设计思想，将原本在程序中手动创建对象的控制权，交由给Spring框架来管理。

IOC容器可以当做为一个工厂，当我们需要创建一个对象的时候，只需要配置好配置文件/注解即可，完全不用考虑对象是如何被创建出来的。

![img](E:\homework\Markdown\spring\img\SpringIOC初始化过程.png)



这就是依赖倒置原则——把原本的高层建筑依赖底层建筑“倒置”过来，变成底层建筑依赖高层建筑。高层建筑决定需要什么，底层去实现这样的需求，但是高层并不用管底层是怎么实现的。这样就不会出现前面的“牵一发动全身”的情况。

![v2-ee924f8693cff51785ad6637ac5b21c1_720w](E:\homework\Markdown\spring\Untitled.assets\v2-ee924f8693cff51785ad6637ac5b21c1_720w.jpg)

#### AOP

AOP（Aspect-Oriented Programming：面向切面编程）：将业务无关的，但被业务所共同调用（例如事务处理、日志管理、权限控制）的逻辑封装起来，以此来减少系统的重复代码，降低模块的耦合度。

Spring AOP基于动态代理：

- 若代理对象实现了接口，则AOP会使用JDK Proxy。
- 若代理对象没有使用接口的对象，AOP就会使用Cglib。



![img](E:\homework\Markdown\spring\img\SpringAOPProcess.jpg)

#### Spring bean

1. bean的作用域
   - singleton：唯一bean实例，Spring的bean默认都是单例的。
   - prototype：每次请求都会创建一个bean实例。
   - request：每一次HTTP请求都会产生一个bean，该bean只在Http Request内有效。
   - session：每一次HTTP请求都会产生一个bean，该bean只在Http session内有效。
   - global-session：全局session作用域，仅仅基于portlet的web应用才有意义。Spring5已取消

#### bean的线程安全问题

例 bean 存在线程问题，主要是因为当多个线程操作同一个对象的时候，对这个对象的非静态成员变量的写操作会存在线程安全问题。

常见的有两种解决办法：

1. 在Bean对象中尽量避免定义可变的成员变量（不太现实）。
2. 在类中定义一个ThreadLocal成员变量，将需要的可变成员变量保存在 ThreadLocal 中（推荐的一种方式）。

#### @Component和@Bean的区别

1. 作用对象不同：@Component作用于类，@Bean作用于方法。

2. `@Component`通常是通过类路径扫描来自动侦测以及自动装配到Spring容器中。

   `@Bean` 注解通常是我们在标有该注解的方法中定义产生这个 bean,`@Bean`告诉了Spring这是某个类的示例，当我需要用它的时候还给我。

3. `@Bean` 注解比 `Component` 注解的自定义性更强，而且很多地方我们只能通过 `@Bean` 注解来注册bean。比如当我们引用第三方库中的类需要装配到 `Spring`容器时，则只能通过 `@Bean`来实现。

#### 将一个类声明为Spring的 bean 的注解有哪些?

我们一般使用 `@Autowired` 注解自动装配 bean，要想把类标识成可用于 `@Autowired` 注解自动装配的 bean 的类,采用以下注解可实现：

- `@Component` ：通用的注解，可标注任意类为 `Spring` 组件。如果一个Bean不知道属于哪个层，可以使用`@Component` 注解标注。
- `@Repository` : 对应持久层即 Dao 层，主要用于数据库相关操作。
- `@Service` : 对应服务层，主要涉及一些复杂的逻辑，需要用到 Dao层。
- `@Controller` : 对应 Spring MVC 控制层，主要用户接受用户请求并调用 Service 层返回数据给前端页面

#### Spring中的bean生命周期

![img](E:\homework\Markdown\spring\img\717817-20180522141553606-1691095215.png)

![img](E:\homework\Markdown\spring\img\70)