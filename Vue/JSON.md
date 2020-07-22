# JSON

## JSON 概述

JSON（JavaScript Object Notation，js对象标记）

使用纯文本的格式来表示和存储数据

## JSON和JavaScript对象互转

### JavaScript对象的定义

```javascript
var obj = {
    name:"zhangsan",
    age	:"12",
  	sex :"male"
}
console.log(obj);
{name: "zhangsan", age: "12", sex: "male"}
```

js转JSON：

```
var myjson = JSON.stringify(obj); 
console.log(myjson);
{"name":"zhangsan","age":"12","sex":"male"}
```

JSON 转js

```
console.log(JSON.parse(myjson));
{name: "zhangsan", age: "12", sex: "male"}
```

## Controller返回JSON数据

解析工具

1. Jackson
2. 阿里巴巴的fastjson

### 在controller的配置

1. 加上@ResponseBody，作用：不会走视图解析器，而是直接返回一个字符串
2. 也可以在类上加@RestController，则会默认在所有类上添加@ResponseBody

### jackson使用

```
		<dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.11.0</version>
        </dependency>

ObjectMapper mapper = new ObjectMapper();
String str = mapper.writeValueAsString(person);
```

### fastjson使用

```
 <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.4</version>
</dependency>

String s = JSON.toJSONString(person);
```

## 前端简述

## CSS 预处理器

为CSS添加一些编程的特性，经过编译后，转化为正常的css文件，以供正常项目使用

常见CSS的预处理器：

SASS：基于Ruby，通过服务端处理，解析效率高，

LASS：基于Node.js，通过客户端处理，解析效率较SASS低

![image-20200613110440963](E:\homework\Markdown\Vue\JSON.assets\image-20200613110440963.png)