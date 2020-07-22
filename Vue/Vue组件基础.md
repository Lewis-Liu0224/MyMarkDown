# 组件基础

## 基本实例

```javascript
// 定义一个名为 button-counter 的新组件
Vue.component('button-counter', {
  data: function () {
    return {
      count: 0
    }
  },
  template: '<button v-on:click="count++">You clicked me {{ count }} times.</button>'
})
```

在HTML中这样使用

```html
<div id="components-demo">
  <button-counter></button-counter>
</div>
```

组件可以看做是可复用的Vue实例，所有他们与`new Vue`接受相同的选项，例如`data、computed、watch、methods`以及生命钩子等。仅有的例外像`el`这样的根实例特有的选项

### 组件的复用

可以将组件进行任意次数的复用：

```html
<div id="components-demo">
  <button-counter></button-counter>
  <button-counter></button-counter>
  <button-counter></button-counter>
</div>
```

注意，每个组件都有自己的Vue实例，也就是说他们都独立维护自己的count值

### data必须是一个函数

组件维护自己作用域的前提是，一个组件的data选项必须是一个函数：

```javascript
data:function(){
    return{
        count:0
    }
}
```

### 组件的实际使用

通常一个应用会以一颗嵌套的组件树来组织：

![Component Tree](https://cn.vuejs.org/images/components.png)

例如，你可能会有页头、侧边栏、内容区等组件，每个组件又包含了其它的像导航链接、博文之类的组件。

为了能在模板中使用，这些组件必须先注册以便 Vue 能够识别。这里有两种组件的注册类型：**全局注册**和**局部注册**。至此，我们的组件都只是通过 `Vue.component` 全局注册的：

```javascript
Vue.component('my-component-name', {
  // ... options ...
})
```

### 通过Prop向子组件传递数据

Prop是可以在在view model上注册一些自定义的attribute。当一个值传给prop attribute的时候，传递的值包括其名称，则成为view中组件实例的一个property，如下例：

```javascript
Vue.component('blog-post', {
  props: ['title'],
  template: '<h3>{{ title }}</h3>'
})
```

一个组件默认可以拥有任意数量的 prop，任何值都可以传递给任何 prop。在上述模板中，你会发现我们能够在组件实例中访问这个值，就像访问 `data` 中的值一样。

一个 prop 被注册之后，你就可以像这样把数据作为一个自定义 attribute 传递进来：

```HTML
<blog-post title="My journey with Vue"></blog-post>
<blog-post title="Blogging with Vue"></blog-post>
<blog-post title="Why Vue is so fun"></blog-post>
```

### 单个根元素

当构建一个`<blog-post>`组件时，你的模板最终会包含的东西远不止一个子元素，例如：

```html
  <h3>{{ title }}</h3>
  <div v-html="content"></div>
```

但是这样写会显示一个错误，并解释道 **every component must have a single root element (每个组件必须只有一个根元素)**。这时可以将模板的内容包裹在一个父元素内，来修复这个问题：

```html
<div class="blog-post">
  <h3>{{ title }}</h3>
  <div v-html="content"></div>
</div>
```

### 监听子组件事件

当有这样一个情况：在使用组件时需要和我们的父级组件进行沟通

应这样使用：

1. 在父级组件中使用`v-on`注册该子组件实例的事件

   ```html
   <blog-post
     ...
     v-on:enlarge-text="postFontSize += 0.1"
   ></blog-post>
   ```

2. 在子组件中通过调用内建的 **`$emit`** 方法并传入事件名称来触发一个事件：

   ```html
   <button v-on:click="$emit('enlarge-text')">
     Enlarge text
   </button>
   ```

### 使用事件抛出一个值

1. 在子组件中这样设定

   ```html
   <button v-on:click="$emit('enlarge-text', 0.1)">
     Enlarge text
   </button>
   ```

2. 在父级组件监听该事件时，通过$event访问到被抛出的这个值

   ```html
   <blog-post
     ...
     v-on:enlarge-text="postFontSize += $event"
   ></blog-post>
   ```

   

### 在组件上使用`v-model`

自定义事件也可以用于创建支持 `v-model` 的自定义输入组件。记住：

```
<input v-model="searchText">
```

等价于：

```
<input
  v-bind:value="searchText"
  v-on:input="searchText = $event.target.value"
>
```

当用在组件上时，`v-model` 则会这样：

```
<custom-input
  v-bind:value="searchText"
  v-on:input="searchText = $event"
></custom-input>
```

为了让它正常工作，这个组件内的 `<input>` 必须：

- 将其 `value` attribute 绑定到一个名叫 `value` 的 prop 上
- 在其 `input` 事件被触发时，将新的值通过自定义的 `input` 事件抛出

写成代码之后是这样的：

```
Vue.component('custom-input', {
  props: ['value'],
  template: `
    <input
      v-bind:value="value"
      v-on:input="$emit('input', $event.target.value)"
    >
  `
})
```

现在 `v-model` 就应该可以在这个组件上完美地工作起来了：

```
<custom-input v-model="searchText"></custom-input>
```

### 解析DOM模板时的注意事项

对于有些HTML元素，诸如`<ul>、<ol>、<table>和<select>`，对于哪些元素可以出现在其内部是有严格的限制，比如说`<li>、<tr>和<option>`只能出现在某些特定元素的内部

故可能导致我们在使用者写有约束条件的元素时遇到一些问题：

```html
<table>
    <blog-post-row></blog-post-row>
</table>
```

这个自定义组件会被作为无效的内容提升到外部，并导致最终渲染结果出错。

针对于这种现象，Vue提供了特殊的`is`attribute给了我们一个变通的方法

```html
<table>
    <tr is="blog-post-row"></tr>
</table>
```

