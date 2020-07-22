# Class 与Style绑定

## 绑定HTML Class

### 对象语法

我们可以传给 `v-bind:class` 多个对象，以动态地切换 class，且还可以与普通的class attribute共存：

```html
<div
  class="static"
  v-bind:class="{ active: isActive, 'text-danger': hasError }"
></div>
```

```javascript
data: {
  isActive: true,
  hasError: false
}
```

结果渲染为：

```html
<div class="static active"></div>
```

此外还可以有另一种写法，即将对象写在modelview中，而不是写在view中，例如

```html
<div v-bind:class="classObject"></div>
```

```javascript
data: {
  classObject: {
    active: true,
    'text-danger': false
  }
}
```

### 对于组件的class

在view中使用组件并添加一些class，这些class会添加到该组件的根元素上。而这些元素上已经存在的class不会被覆盖

## 条件渲染

### v-if

v-if指令用于条件性地渲染一块内容。这块内容只会在指令的表达式返回truthy值的时候被渲染

也可以用v-else添加一个"else"块：

### 在`<template>`元素上使用v-if条件渲染分组

因为v-if是一个指令，只能添加到一个元素上，如果想要切换多个元素，此时可以把一个`<template>`元素当做不可见的包裹元素，并在上面使用v-if。最终的渲染结果将不包含`<template>`元素

```html
<template v-if="ok">
  <h1>Title</h1>
  <p>Paragraph 1</p>
  <p>Paragraph 2</p>
</template>
```

### v-else

v-else元素必须紧跟在带v-if或v-else-if的元素的后面，否则它将不会被识别。

### 用key管理科复用的元素

Vue会尽可能的高效渲染元素，通常会复用已有的元素而不是从头开始渲染。

```html
<template v-if="loginType === 'username'">
  <label>Username</label>
  <input placeholder="Enter your username">
</template>
<template v-else>
  <label>Email</label>
  <input placeholder="Enter your email address">
</template>
```

那么在上面的代码中切换`loginType`将不会清理用户已经输入的内容。因为两个模板使用了相同的元素，`<input>`不会被替换掉，仅仅是替换了它的`placeholder`。

但是当需要两个元素完全独立的情况时（即不要复用他们），则可以添加一个具有唯一值的key attribute即可：

```html
<template v-if="loginType === 'username'">
  <label>Username</label>
  <input placeholder="Enter your username" key="username-input">
</template>
<template v-else>
  <label>Email</label>
  <input placeholder="Enter your email address" key="email-input">
</template>
```

### v-show

根据一个条件选择是否展示元素，用法：

```html
<h1 v-show="ok">Hello!</h1>
```

注意：v-show不支持`<template>`元素，也不支持`v-else`

### v-show和v-if的区别

1. v-show：不管条件真假，v-show都会被渲染，只是为假是改变了该标签的css值
2. v-if：当条件为假时，则什么都不做，也不会被渲染至view内，直到为真时，才会开始渲染条件块

一般来说v-if的切换开销更大，如果需要频繁切换，则使用v-show较好

## 列表渲染

### 用v-for把一个数组对应为一组元素

使用v-for指令来渲染view-model中的一个数组。该命令需要使用`item in items`的语法。其中`items`是view model中的数组，`item`则是被迭代的数组元素别名

```html
<ul id="example-1">
  <li v-for="item in items" :key="item.message">
    {{ item.message }}
  </li>
</ul>
```

```javascript
var example1 = new Vue({
  el: '#example-1',
  data: {
    items: [
      { message: 'Foo' },
      { message: 'Bar' }
    ]
  }
})
```

此外v-for还支持一个可选的第二个参数，即当前元素的索引

```html
<ul id="example-2">
  <li v-for="(item, index) in items">
    {{ parentMessage }} - {{ index }} - {{ item.message }}
  </li>
</ul>
```

### 除了遍历一个数组，还可以遍历一个对象

```html
<div v-for="(value, name, index) in object">
  {{ index }}. {{ name }}: {{ value }}
</div>
```

```javascript
new Vue({
  el: '#v-for-object',
  data: {
    object: {
      title: 'How to do lists in Vue',
      author: 'Jane Doe',
      publishedAt: '2016-04-10'
    }
  }
})
```

从上例可以看出，`value、name、index`好像是固定写法

## 数组更新检测

### 变更方法

Vue 将被侦听的数组的变更方法进行了包裹，所以它们也将会触发视图更新。这些被包裹过的方法包括：

- `push()`
- `pop()`
- `shift()`
- `unshift()`
- `splice()`
- `sort()`
- `reverse()`

你可以打开控制台，然后对前面例子的 `items` 数组尝试调用变更方法。比如 `example1.items.push({ message: 'Baz' })`。

### 替换数组

变更方法，顾名思义，会变更调用了这些方法的原始数组。相比之下，也有非变更方法，例如 `filter()`、`concat()` 和 `slice()`。它们不会变更原始数组，而**总是返回一个新数组**。当使用非变更方法时，可以用新数组替换旧数组：

```javascript
example1.items = example1.items.filter(function (item) {
  return item.message.match(/Foo/)
})
```

你可能认为这将导致 Vue 丢弃现有 DOM 并重新渲染整个列表。幸运的是，事实并非如此。Vue 为了使得 DOM 元素得到最大范围的重用而实现了一些智能的启发式方法，所以用一个含有相同元素的数组去替换原来的数组是非常高效的操作。

### 在`<template>`上使用v-for

类似于v-if，你也可以利用带有v-for的`<template>`来循环渲染一段包含多个元素的内容。比如

```html
<ul>
  <template v-for="item in items">
    <li>{{ item.msg }}</li>
    <li class="divider" role="presentation"></li>
  </template>
</ul>
```

### 在组件上使用v-for

在2.2.0+的版本里，当在组件上使用v-for时，key现在是必须的

注意：任何数据都不会被自动传递到组件，因为组件拥有自己的独立的作用域，所以我们要使用prop：

```html
<my-component
  v-for="(item, index) in items"
  v-bind:item="item"
  v-bind:index="index"
  v-bind:key="item.id"
></my-component>
```

注意：

1. 不建议直接将item放到组件内，这样会使v-for和组件运作紧密耦合（例如有时需要用该组件遍历用户的文章，有时需要遍历该用户的名称）
2. 这里的`is="todo-item"`，相当于`<todo-item>`相同，但是可以避开一些浏览器解析错误

下面是一个完整的v-for和组件的例子

```html
<div id="todo-list-example">
  <form v-on:submit.prevent="addNewTodo">
    <label for="new-todo">Add a todo</label>
    <input
      v-model="newTodoText"
      id="new-todo"
      placeholder="E.g. Feed the cat"
    >
    <button>Add</button>
  </form>
  <ul>
    <li
      is="todo-item"
      v-for="(todo, index) in todos"
      v-bind:key="todo.id"
      v-bind:title="todo.title"
      v-on:remove="todos.splice(index, 1)"
    ></li>
  </ul>
</div>
```

```javascript
Vue.component('todo-item', {
  template: '\
    <li>\
      {{ title }}\
      <button v-on:click="$emit(\'remove\')">Remove</button>\
    </li>\
  ',
  props: ['title']
})

new Vue({
  el: '#todo-list-example',
  data: {
    newTodoText: '',
    todos: [
      {
        id: 1,
        title: 'Do the dishes',
      },
      {
        id: 2,
        title: 'Take out the trash',
      },
      {
        id: 3,
        title: 'Mow the lawn'
      }
    ],
    nextTodoId: 4
  },
  methods: {
    addNewTodo: function () {
      this.todos.push({
        id: this.nextTodoId++,
        title: this.newTodoText
      })
      this.newTodoText = ''
    }
  }
})
```

