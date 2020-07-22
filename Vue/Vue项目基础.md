# Vue项目基础

## 前端模块化开发

## webpack

webpack背景：当前浏览器使用的规范为`ES5.1`,而一些前端的框架使用的规范为`ES6`，这是需要用webpack来将ES6规范的项目降级为ES5.1

### webpack.config.js

1. entry:入口文件，指定WebPack用哪个文件作为项目的入口
2. output：输出，指定WebPack把处理完成的文件放置到指定路径
3. module：模块，用于处理各种类型的文件
4. plugins：插件，如：热更新、代码重用等
5. resolve：设置路径指向
6. watch：监听，用于设置文件改动后直接打包

### npm命令解释：

1. `npm install moduleName`：安装模块到项目目录下
2. `npm install -g moduleName`：-g的意思是将模块安装到全局，具体安装到磁盘的哪个位置，要看npm config prefix的位置
3. `npm install -save moduleName`：--save的意思是将模块安装到项目目录下，并在package文件的dependencies的节点写入依赖,-S为该命令的缩写
4. `npm install -svae-dev moduleName`：该命令的意思是将模块安装到项目目录下，并在package文件的devDependencied节点写入依赖，-D为该命令的缩写

## vue-router路由

