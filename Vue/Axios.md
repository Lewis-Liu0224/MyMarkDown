# Axios

运用在浏览器和NodeJS的异步通信框架，主要作用是实现AJAX异步通信

Axios的cdn

```
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
```

钩子函数：

```JavaScript
    var vm = new Vue({
        el: "#app",
        mounted(){
            axios.get('./person.json').then(response=>(console.log(response.data)))
        }
    });
    
结果：
people: Array(3)
0:
email: "brett@newInstance.com"
firstName: "Brett"
lastName: "McLaughlin"
__proto__: Object
1:
email: "jason@servlets.com"
firstName: "Jason"
lastName: "Hunter"
__proto__: Object
2:
email: "elharo@macfaq.com"
firstName: "Elliotte"
lastName: "Harold"
__proto__: Object
```

将请求到的信息传递页面

```javascript

<div id="app">
<div>{{info.people[0].firstName}}</div>
<div>{{info.people[1].email}}</div>
</div>

    var vm = new Vue({
        el: "#app",
        data(){
            return{
                info:{
                    people:[
                        {
                            firstName:  null,
                            lastName:   null,
                            email:      null
                        }
                    ]
                }
            }
        },
        mounted(){
            axios.get('./person.json').then(response=>(this.info=response.data))
        }
    });

```

### slot 插槽

