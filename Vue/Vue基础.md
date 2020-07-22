# Vue基础

## Vue介绍

### 第一个Vue程序

第一个Vue程序

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<div id="app">
    {{message}}
</div>
<script type="text/javascript" src="https://vuejs.org/js/vue.min.js"></script>
<script>
    var vm = new Vue({
        el: "#app",
        data: {
            message: "hello vue"
        }
    });
</script>
</body>
</html>
```



### view与viewModel的数据双向绑定

![image-20200613112242440](E:\homework\Markdown\Vue\前端简述.assets\image-20200613112242440.png)

这里唯一的例外是使用 `Object.freeze()`，这会阻止修改现有的 property，也意味着响应系统无法再*追踪*变化。(即将viewmodel与view解绑)

Vue实例还有一些实例变量和方法，且有$的前缀，以便和用户自定义的变量或方法区分开，例如：

```javascript
var data = { a: 1 }
var vm = new Vue({
  el: '#example',
  data: data
})

vm.$data === data // => true
vm.$el === document.getElementById('example') // => true

// $watch 是一个实例方法
vm.$watch('a', function (newValue, oldValue) {
  // 这个回调将在 `vm.a` 改变后调用
})
```

### 实例生命周期的钩子：

每个实例在被创建时都要经过一系列的初始化过程，例如需要设置数据监听、编译模板、将实例挂载到DOM并在数据变化时更新DOM等。同时在这个过程中也会运行一些叫做生命周期钩子的函数，这给了用户在不同阶段添加自己代码的机会。

例如`created`钩子可以用来在一个实例被创建之后执行代码

```javascript
new Vue({
  data: {
    a: 1
  },
  created: function () {
    // `this` 指向 vm 实例
    console.log('a is: ' + this.a)
  }
})
// => "a is: 1"
```

### 生命周期图示：

![Vue 实例生命周期](https://cn.vuejs.org/images/lifecycle.png)

### Vue的作用：完全解耦了view和model层，是MVVM模式的实现者



## 文本

数据绑定最常见的形式就是使用"Mustache"语法（双大括号）的文本插值

```html
<span>Message:{{ msg }}</span>
```

将Model View中的msg的与该大括号标签内容绑定，无论何时，两处的值只要发生了改变，另一处的其他值则会更新

### 在数据绑定中使用JavaScript表达式：

例如：

```html
{{ number + 1 }}

{{ ok ? 'YES' : 'NO' }}

{{ message.split('').reverse().join('') }}

<div v-bind:id="'list-' + id"></div>
```

以下是反例：

```html
<!-- 这是语句，不是表达式 -->
{{ var a = 1 }}

<!-- 流控制也不会生效，请使用三元表达式 -->
{{ if (ok) { return message } }}
```



### 使用`v-once指令` ：

该命令也能执行一次性地插值，当数据改变时，插值处的内容不会更新

```html
<span v-once>这个将不会改变：{{ msg }}</span>
```

### 当文本内容为HTML代码时：

双大括号会将数据解释为普通文本，而非HTML代码。为了输出真正的HTML代码，需要使用`v-html`指令：

```html
Using mustaches:<span style="color:red">This should be red</span>
Using v-html directive:This should be red
```

### v-bind：

Mustache语，语法不能作用在HTML attribute上，遇到这种情况应该使用`v-bind`指令：

例如：将username绑定在该HTML的id上

```html
<div v-bind:id="username"></di>
```

注意，对于布尔attribute（它们只要存在就意味着true）,v-bind工作起来就略有不同，比如：

```html
<button v-bind:disabled="isButtonDisabled">
    Button
</button>
```

如果`isButtonDisabled`的值是null、undefined、或false，则disabled的attribute不会出现在渲染出来的button元素中

### v-指令

- v-bind: href
- v-on:click
- v-if

#### v-else,v-else-if:

### 动态参数

使用方括号将javascript表达式作为一个指令的参数：

```html
<a v-bind:[attributeName]="url"> ... </a>
```

例如，如果你的 Vue 实例有一个 `data` property `attributeName`，其值为 `"href"`，那么这个绑定将等价于 `v-bind:href`。

同样地，你可以使用动态参数为一个动态的事件名绑定处理函数：

```html
<a v-on:[eventName]="doSomething"> ... </a>
```

在这个示例中，当 `eventName` 的值为 `"focus"` 时，`v-on:[eventName]` 将等价于 `v-on:focus`。

#### 对动态参数表达式的约束

1. 有些字符在HTML attribute名里命名是无效的，比如空格和引号
2. 还需要避免使用大写字符来命名键名，因为浏览器会把attribute名全部强制转为小写

#### 修饰符

以半角句号`.`指明的特殊后缀，用于指出一个指令应该以特殊方式绑定。

#### 缩写

Vue为`v-bind`和`v-on`这两个最常用的指令，提供了特定简写：

1. v-bind缩写

   ```html
   <!-- 完整语法 -->
   <a v-bind:href="url">...</a>
   
   <!-- 缩写 -->
   <a :href="url">...</a>
   
   <!-- 动态参数的缩写 (2.6.0+) -->
   <a :[key]="url"> ... </a>
   ```

2. v-on缩写

   ```html
   <!-- 完整语法 -->
   <a v-on:click="doSomething">...</a>
   
   <!-- 缩写 -->
   <a @click="doSomething">...</a>
   
   <!-- 动态参数的缩写 (2.6.0+) -->
   <a @[event]="doSomething"> ... </a>
   ```


## vue计算属性

模板内的表达式的初衷是用于简单运算。在模板中放入太多的逻辑还让模板过重，且难以维护。

```html
<div id="example">
  {{ message.split('').reverse().join('') }}
</div>
```

所以，对于任何复杂的逻辑，你都应当使用计算属性

### 基础例子

```html
<div id="example">
  <p>Original message: "{{ message }}"</p>
  <p>Computed reversed message: "{{ reversedMessage }}"</p>
</div>

```

```javascript
var vm = new Vue({
  el: '#example',
  data: {
    message: 'Hello'
  },
  computed: {
    // 计算属性的 getter
    reversedMessage: function () {
      // `this` 指向 vm 实例
      return this.message.split('').reverse().join('')
    }
  }
})

//结果：
//Original message: "Hello"
//Computed reversed message: "olleH"
```

在上面这个例子中，vm.reversedMessage的值始终取决于vm.message的值。

故当vm.message发生改变时，所有依赖vm.reversedMessage的绑定也会更新。

### 计算属性和方法

上例除了使用计算属性，还可以通过调用方法来达到同样的效果

```html
<p>
    Reversed message :"{{ reversedMessage()}}"
</p>
```

```javascript
//在组件中
methods: {
  reversedMessage: function () {
    return this.message.split('').reverse().join('')
  }
}
```

调用方法时，每次都需要进行计算，当需要计算必然产生系统开销，那如果这个结果是不经常变化的呢？此时就可以考虑将这个结果缓存起来，采用计算属性可以很方便的做到这一点，计算属性的主要特性就是为了将不经常变化的计算结果进行缓存，以节约我们的系统开销

```JavaScript
	//调用methods需要加括号
	<div>{{cuT1()}}</div>
	//调用computed不需要加括号
    <div>{{cuT2}}</div>


var vm = new Vue({
        el: "#app",
        data:{
            message:'Hello world',
            msg:"mother fucker"
        },
        methods:{
            cuT1: function () {
                return Date.now();
            }
        },
        computed:{
            cuT2:function () {
                this.message;
                return Date.now();
            }
        }
    });
```

经过测试，发现每次在控制台调用cuT2得到得到数据都相同，但是一旦改变message的值后，调用cuT2返回的值就改变了（类似于缓存

###  计算属性VS侦听属性

在一些情况下，需要监听和响应实例上的数据变动，很容易滥用watch，然而更好的做法是使用计算属性而不是命令式的watch回调 

### 计算属性的setter

计算属性默认只有 getter，不过在需要时你也可以提供一个 setter：

```javascript
// ...
computed: {
  fullName: {
    // getter
    get: function () {
      return this.firstName + ' ' + this.lastName
    },
    // setter
    set: function (newValue) {
      var names = newValue.split(' ')
      this.firstName = names[0]
      this.lastName = names[names.length - 1]
    }
  }
}
// ...
```

现在再运行 `vm.fullName = 'John Doe'` 时，setter 会被调用，`vm.firstName` 和 `vm.lastName` 也会相应地被更新。

### v-for

```javascript
<div id="app">

    <li v-for="item in items">
        {{item.msg}}
    </li>
</div>

<script type="text/javascript" src="https://vuejs.org/js/vue.min.js"></script>
<script>
    var vm = new Vue({
        el: "#app",
        data: {
            items: [
                {msg: "A"},
                {msg: "B"}
            ]
        }
    });
```

### v-on

### v-model

### vue组件

```javascript
<div id="app">
    <component v-for="item in items" v-bind:msg="item"></component>
</div>
<script type="text/javascript" src="https://vuejs.org/js/vue.min.js"></script>
<script>

    Vue.component("component",{
        props: ["msg"],
        template: '<li>{{msg}}</li>'
    });

    var vm = new Vue({
        el: "#app",
        data: {
            items: ["java","Linux","前端"]
        }
    });
</script>
```



## 多查官方文档