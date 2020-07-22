# IO操作

## File文件

构造File对象时，即可以传入绝对路径，也可以传入相对路径。绝对路径是可以从目录开头的完整路径。

在Windows中使用`\`作为路径分隔符，在Java中需要用`\\`表示一个`\`。Linux使用`/`作为路径分隔符。

在传入相对路径时，注意`.`的使用：

```java
// 假设当前目录是C:\Docs
File f1 = new File("sub\\javac"); // 绝对路径是C:\Docs\sub\javac
File f3 = new File(".\\sub\\javac"); // 绝对路径是C:\Docs\sub\javac
File f3 = new File("..\\sub\\javac"); // 绝对路径是C:\sub\javac
```

`.`表示当前目录，`..`表示上级目录

### 通过File获取路径：

File对象有3种形式表示的路径，一种是`getPath()`，返回构造方法传入的路径，一种是`getAbsolutePath()`，返回绝对路径，一种是`getCanonicalPath`，它和绝对路径类似，但是返回的是规范路径。

```java
File f = new File("..");
System.out.println(f.getPath());//..
System.out.println(f.getAbsolutePath());//E:\homework\blue_brige_compitation\testDemo2\..
System.out.println(f.getCanonicalPath());//E:\homework\blue_brige_compitation
```

### File的路径分隔符

为了兼容Windows和Linux的路径分隔符不同，File对象有一个静态变量用于表示当前平台的系统分隔符。

### File对象的判断

- `boolean isDirectory():`对象是否已一个已存在目录
- `boolean isFile()`：对象是否是已存在文件
- `boolean canRead()`：是否可读；
- `boolean canWrite()`：是否可写；
- `boolean canExecute()`：是否可执行；
- `long length()`：文件字节大小。

### 创建和删除文件和文件目录

`file.createNewFile()`进行创建文件操作，若创建成功则返回true，若已存在则返回False。

`file.delete()`进行删除操作，若成功返回true否则false。

`boolean mkdir()`：创建当前File对象表示的目录；

`boolean mkdirs()`：创建当前File对象表示的目录，并在必要时将不存在的父目录也创建出来；

`boolean delete()`：删除当前File对象表示的目录，当前目录必须为空才能删除成功。

### 遍历文件和目录

当File对象表示一个目录时，可以使用`list()`和`listFiles()`列出目录下的文件和子目录名。`listFiles()`提供了一系列重载方法，可以过滤不想要的文件和目录：

```java
File f = new File("C:\\Windows");
        File[] fs1 = f.listFiles(); // 列出所有文件和子目录
        printFiles(fs1);
        File[] fs2 = f.listFiles(new FilenameFilter() { // 仅列出.exe文件
            public boolean accept(File dir, String name) {
                return name.endsWith(".exe"); // 返回true表示接受该文件
            }
        });
```

## Path

Java标准库还提供了一个`Path`对象，它位于`java.nio.file`包。`Path`对象和`File`对象类似，但操作更加简单：

```java
Path p1 = Paths.get(".", "project", "study"); // 构造一个Path对象
        System.out.println(p1);
        Path p2 = p1.toAbsolutePath(); // 转换为绝对路径
        System.out.println(p2);
        Path p3 = p2.normalize(); // 转换为规范路径
        System.out.println(p3);
        File f = p3.toFile(); // 转换为File对象
        System.out.println(f);
        for (Path p : Paths.get("..").toAbsolutePath()) { // 可以直接遍历Path
            System.out.println("  " + p);
        }
```

如果需要对目录进行复杂的拼接、遍历等操作，使用`Path`对象更方便。

## `InputStream`

`InputStream`并不是一个接口，而是一个抽象类，它定义了一个重要的方法就是`int read()`

```java
public abstract int read() throws IOException;//读取下一个字节，返回int值（0~255）。到末尾返回-1
```

`FileInputStream`是`InputStream`的一个子类。实例方法如下

```java
try{
	InputStream input = new FileInputStream("src/readme.txt");
    int n;
    while((n = input.read()) != -1){
        System.out.println(n);
    }
} finally {
    if (input != null ){ input.close();}
}
```

注意IO操作需要在末尾添加close()方法来关闭流，使用`try ... finally`来编写可能有些复杂。

推荐Java7引入的新的`try(resource)`得语法，只需要编写`try`语句，让编译器自动为我们关闭资源

```java
public void readFile() throws IOException {
    try (InputStream input = new FileInputStream("src/readme.txt")) {
        int n;
        while ((n = input.read()) != -1) {
            System.out.println(n);
        }
    } // 编译器在此自动为我们写入finally并调用close()
}
```

### 缓冲

在读取文件时，一次一个字节的效率并不是很高。利用缓冲区一次性读取多个字节效率往往要高很多。InputStream提供了两个重载方法来支持读取多个字节

- `int read(byte[] b)`：读取若干字节并填充到`byte[]`数组，返回读取的字节数
- `int read(byte[] b, int off, int len)`：指定`byte[]`数组的偏移量和最大填充数

以上两种方法的返回值不再是字节的int值，而是返回实际读取了多少字节。

```java
public void readFile() throws IOException {
    try (InputStream input = new FileInputStream("src/readme.txt")) {
        // 定义1000个字节大小的缓冲区:
        byte[] buffer = new byte[1000];
        int n;
        while ((n = input.read(buffer)) != -1) { // 读取到缓冲区
            System.out.println("read " + n + " bytes.");
        }
    }
}
```

### 阻塞

在调用`InputStream`的`read()`方法读取数据时，我们说`read()`方法是阻塞（Blocking）的。它的意思是，对于下面的代码：

```java
int n;
n = input.read(); // 必须等待read()方法返回才能执行下一行代码
int m = n;
```

执行到第二行代码时，必须等`read()`方法返回后才能继续。因为读取IO流相比执行普通代码，速度会慢很多，因此，无法确定`read()`方法调用到底要花费多长时间。

### ByteArrayInputStream

ByteArrayInputStream 可以在内存中模拟一个InputStream，它是将一个byte[]数组在内存中变成一个InputSteam。常常用于测试

```java
public class Main {
    public static void main(String[] args) throws IOException {
        byte[] data = { 72, 101, 108, 108, 111, 33 };
        try (InputStream input = new ByteArrayInputStream(data)) {
            String s = readAsString(input);
            System.out.println(s);
        }
    }

    public static String readAsString(InputStream input) throws IOException {
        int n;
        StringBuilder sb = new StringBuilder();
        while ((n = input.read()) != -1) {
            sb.append((char) n);
        }
        return sb.toString();
    }
}
```

## OutputSream

和InputStream相同，OutputSteam也是抽象类，并且定义了一个重要的方法：void write（int b）。

```java
public abstract void write(int b) throws IOException;
```

虽然传的是32位int参数，但只会写入一个字节，即只写入int最低8位表示的字节部分(相当于 b & 0xff)

### flush()

和InputStream不同的是，outputStream还提供了flush()方法，它的作用是将缓冲区内容，真正输出到目的地。

flush()的背景是，在向磁盘和网络写入数据时，处于效率考虑，往往将写入的存储在缓冲区，再一次性写入。

以下两种情况会自动调用flush()

1. 缓冲区满了，outputStream会自动调用
2. 调用close（）方法，在关闭之前会自动调用flush()

### FileOuputStream

在写入时，一个字节一个字节的写非常麻烦，通常使用重载方法：void write(byte[])

```java
public void writeFile() throws IOException {
    OutputStream output = new FileOutputStream("out/readme.txt");
    output.write("Hello".getBytes("UTF-8")); // Hello
    output.close();
}
```

和inputStream相似，outputStream也有阻塞和ByteArrayOutputStream()

## Filter模式

InputStream根据来源可以包括：

- `FileInputStream`：从文件读取数据，是最终数据源；
- `ServletInputStream`：从HTTP请求读取数据，是最终数据源；
- `Socket.getInputStream()`：从TCP连接读取数据，是最终数据源；
- ...

如果我们要给`FileInputStream`添加缓冲功能，则可以从`FileInputStream`派生一个类：

```
BufferedFileInputStream extends FileInputStream
```

如果要给`FileInputStream`添加计算签名的功能，类似的，也可以从`FileInputStream`派生一个类：

```
DigestFileInputStream extends FileInputStream
```

如果要给`FileInputStream`添加加密/解密功能，还是可以从`FileInputStream`派生一个类：

```
CipherFileInputStream extends FileInputStream
```

![image-20200428103418735](E:\homework\Markdown\img\image-20200428103418735.png)

这仅仅是FileInputStream的设计，如果是InputStream子类则会更多。

为了解决这种问题，JDK首先将InputStream分为两大类：

一类是直接提供数据的基础`InputStream`，例如：

- FileInputStream
- ByteArrayInputStream
- ServletInputStream
- ...

一类是提供额外附加功能的`InputStream`，例如：

- BufferedInputStream
- DigestInputStream
- CipherInputStream
- ...

当我们需要给一个“基础”`InputStream`附加各种功能时，我们先确定这个能提供数据源的`InputStream`，因为我们需要的数据总得来自某个地方，例如，`FileInputStream`，数据来源自文件：

```
InputStream file = new FileInputStream("test.gz");
```

紧接着，我们希望`FileInputStream`能提供缓冲的功能来提高读取的效率，因此我们用`BufferedInputStream`包装这个`InputStream`，得到的包装类型是`BufferedInputStream`，但它仍然被视为一个`InputStream`：

```
InputStream buffered = new BufferedInputStream(file);
```

最后，假设该文件已经用gzip压缩了，我们希望直接读取解压缩的内容，就可以再包装一个`GZIPInputStream`：

```
InputStream gzip = new GZIPInputStream(buffered);
```

无论我们包装多少次，得到的对象始终是`InputStream`，我们直接用`InputStream`来引用它，就可以正常读取：

![image-20200428103848623](E:\homework\Markdown\img\image-20200428103848623.png)

上述这种通过一个“基础”组件再叠加各种“附加”功能组件的模式，称之为Filter模式（或者装饰器模式：Decorator）。它可以让我们通过少量的类来实现各种功能的组合：

![image-20200428103905770](E:\homework\Markdown\img\image-20200428103905770.png)

类似的，`OutputStream`也是以这种模式来提供各种功能：

![image-20200428103920246](E:\homework\Markdown\img\image-20200428103920246.png)

### 编写FilterInputStream

我们也可以自己编写`FilterInputStream`，以便可以把自己的`FilterInputStream`“叠加”到任何一个`InputStream`中。

下面的例子演示了如何编写一个`CountInputStream`，它的作用是对输入的字节进行计数：

public class Main {
    public static void main(String[] args) throws IOException {
        byte[] data = "hello, world!".getBytes("UTF-8");
        try (CountInputStream input = new CountInputStream(new ByteArrayInputStream(data))) {
            int n;
            while ((n = input.read()) != -1) {
                System.out.println((char)n);
            }
            System.out.println("Total read " + input.getBytesRead() + " bytes");
        }
    }
}

class CountInputStream extends FilterInputStream {
    private int count = 0;

```java
CountInputStream(InputStream in) {
    super(in);
}

public int getBytesRead() {
    return this.count;
}

public int read() throws IOException {
    int n = in.read();
    if (n != -1) {
        this.count ++;
    }
    return n;
}

public int read(byte[] b, int off, int len) throws IOException {
    int n = in.read(b, off, len);
    this.count += n;
    return n;
}
```
}

### 操作ZIP

ZipInputStream是一种FilterInputStream，可以直接读取ZIP包的内容

![image-20200428104312550](E:\homework\Markdown\img\image-20200428104312550.png)

JarInputStream是从ZipInputStream派生，增加的功能是直接读取jar文件里面的MANIFEST.MF文件。因为本质上jar包就是zip包，只是额外附加了一些固定的描述文件。

### 读取zip包

我们来看看`ZipInputStream`的基本用法。

我们要创建一个`ZipInputStream`，通常是传入一个`FileInputStream`作为数据源，然后，循环调用`getNextEntry()`，直到返回`null`，表示zip流结束。

一个`ZipEntry`表示一个压缩文件或目录，如果是压缩文件，我们就用`read()`方法不断读取，直到返回`-1`：

```
try (ZipInputStream zip = new ZipInputStream(new FileInputStream(...))) {
    ZipEntry entry = null;
    while ((entry = zip.getNextEntry()) != null) {
        String name = entry.getName();
        if (!entry.isDirectory()) {
            int n;
            while ((n = zip.read()) != -1) {
                ...
            }
        }
    }
}
```

### 写入zip包

`ZipOutputStream`是一种`FilterOutputStream`，它可以直接写入内容到zip包。我们要先创建一个`ZipOutputStream`，通常是包装一个`FileOutputStream`，然后，每写入一个文件前，先调用`putNextEntry()`，然后用`write()`写入`byte[]`数据，写入完毕后调用`closeEntry()`结束这个文件的打包。

```
try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(...))) {
    File[] files = ...
    for (File file : files) {
        zip.putNextEntry(new ZipEntry(file.getName()));
        zip.write(getFileDataAsBytes(file));
        zip.closeEntry();
    }
}
```

上面的代码没有考虑文件的目录结构。如果要实现目录层次结构，`new ZipEntry(name)`传入的`name`要用相对路径。

## 序列化

序列化是指把一个Java对象变成二进制内容，本质上就是一个byte[]数组，将其变为byte[]数组的原因是，这样就可以通过网络传输到远程。

有序列化就有反序列化，即将一个二进制内容变回Java对象。

若要将一个对象序列化，则要实现一个接口：Serializable接口。该接口没有定义任何方法，这样的空接口称为“标记接口”，实现了标记接口的类仅仅是给自身贴了一个“标记”，并没有增加任何方法。



为了避免这种class定义变动导致的不兼容，Java的序列化允许class定义一个特殊的`serialVersionUID`静态变量，用于标识Java类的序列化“版本”，通常可以由IDE自动生成。如果增加或修改了字段，可以改变`serialVersionUID`的值，这样就能自动阻止不匹配的class版本：

```java
public class Person implements Serializable {
    private static final long serialVersionUID = 2709425275741743919L;
}
```

## Reader

Reader是Java提供的另一个输入流接口，它与InputSteam的区别是，InputStream以一个字节流，一个byte为单位，而Reader是以一个字符，一个char为单位。

| InputStream                         | Reader                                |
| :---------------------------------- | :------------------------------------ |
| 字节流，以`byte`为单位              | 字符流，以`char`为单位                |
| 读取字节（-1，0~255）：`int read()` | 读取字符（-1，0~65535）：`int read()` |
| 读到字节数组：`int read(byte[] b)`  | 读到字符数组：`int read(char[] c)`    |

同理有FileInputStream，故也有FileReader，在使用时要注意，如果文件内有中文要提前指定编码：

```java
Reader reader = new FileReader("src/readme.txt", StandardCharsets.UTF_8);
```

`Reader`还提供了一次性读取若干字符并填充到`char[]`数组的方法：

```
public int read(char[] c) throws IOException
```

### CharArrayReader

`CharArrayReader`可以在内存中模拟一个`Reader`，它的作用实际上是把一个`char[]`数组变成一个`Reader`，这和`ByteArrayInputStream`非常类似：

```
try (Reader reader = new CharArrayReader("Hello".toCharArray())) {
}
```

### StringReader

`StringReader`可以直接把`String`作为数据源，它和`CharArrayReader`几乎一样：

```
try (Reader reader = new StringReader("Hello")) {
}
```

### InputStream和Reader的转换

普通的Reader实际上是基于InputStream改造的，Reader需要从InputSteam中读取字节流（byte），然后根据编码，再转换为char。既然Reader本质上是一个基于InputStream的byte到char的转换器，那么从InputStream到Reader的转换是完全可行的，示例如下：

```java
InputStream input = new FileInputStream("src/readme.txt");
// 变换为Reader:
Reader reader = new InputStreamReader(input, "UTF-8");
```