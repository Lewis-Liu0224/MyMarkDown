# Vue深入了解组件

### 组件名

```
Vue.component('my-component-name',{/* ... */})
```

这些组件是全局注册的。也就是说它们在注册之后可以用在任何新创建的Vue根实例（new Vue）的模板中

### 局部注册

对于一个webpack这样的构件系统，全局注册的组件意味着，你在平时可能会无谓的增加JavaScript，即使你没有用到。

这些情况下，你可以通过一个普通的JavaScript对象来定义组件：

```javascript
var ComponentA = { /* ... */ }
var ComponentB = { /* ... */ }
var ComponentC = { /* ... */ }
```

然后在 `components` 选项中定义你想要使用的组件：

```
new Vue({
  el: '#app',
  components: {
    'component-a': ComponentA,
    'component-b': ComponentB
  }
})
```

然后再`components`选项中定义你想要使用的组件：

```JS
new Vue({
  el: '#app',
  components: {
    'component-a': ComponentA,
    'component-b': ComponentB
  }
})
```

对于`components`对象中的每个property来说，

1. 其property名就是自定义的元素名字
2. 其property值就是这个组件的选项对象。

注意**局部注册的组件在其子组件中\*不可用\***。例如，如果你希望 `ComponentA` 在 `ComponentB` 中可用，则你需要这样写：

```javascript
var ComponentA = { /* ... */ }

var ComponentB = {
  components: {
    'component-a': ComponentA
  },
  // ...
}
```

### 在模块系统中局部注册

一般情况下，建议创建一个components目录，并将每个组件防止在其各自的文件中。

若想在定义组件局部注册之前导入每个你想使用的组件。则可以在ComponentB.js中这样写：

```javascript
import ComponentA from './ComponentA'
import ComponentC from './ComponentC'

export default {
  components: {
    ComponentA,
    ComponentC
  },
  // ...
}
```

这样能使ComponentA和ComponentC在ComponentB的模板使用了

### 基础组件的自动化全局注册

对于一些基础组件，他们在各个组件中频繁被用到，且数量不少，所以会导致很多组件会有一个包含基础组件的长列表：

```javascript
import BaseButton from './BaseButton.vue'
import BaseIcon from './BaseIcon.vue'
import BaseInput from './BaseInput.vue'

export default {
  components: {
    BaseButton,
    BaseIcon,
    BaseInput
  }
}
```

此时若使用了webpack，则可以使用`require.context`只全局注册这些非常通用的基础组件。这里有一份可以让你在应用入口文件（src/main.js）中全局导入基础组件的实例代码：

```javascript
import Vue from 'vue'
import upperFirst from 'lodash/upperFirst'
import camelCase from 'lodash/camelCase'

const requireComponent = require.context(
  // 其组件目录的相对路径
  './components',
  // 是否查询其子目录
  false,
  // 匹配基础组件文件名的正则表达式
  /Base[A-Z]\w+\.(vue|js)$/
)

requireComponent.keys().forEach(fileName => {
  // 获取组件配置
  const componentConfig = requireComponent(fileName)

  // 获取组件的 PascalCase 命名
  const componentName = upperFirst(
    camelCase(
      // 获取和目录深度无关的文件名
      fileName
        .split('/')
        .pop()
        .replace(/\.\w+$/, '')
    )
  )

  // 全局注册组件
  Vue.component(
    componentName,
    // 如果这个组件选项是通过 `export default` 导出的，
    // 那么就会优先使用 `.default`，
    // 否则回退到使用模块的根。
    componentConfig.default || componentConfig
  )
})
```

