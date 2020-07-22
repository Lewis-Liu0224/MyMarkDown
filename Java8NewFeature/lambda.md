## Lambda



### 在Runnable接口使用

```java
public class RunnableTest {
 7   public static void main(String[] args) {
 8     
 9     System.out.println("=== RunnableTest ===");
10     
11     // Anonymous Runnable
12     Runnable r1 = new Runnable(){
13       
14       @Override
15       public void run(){
16         System.out.println("Hello world one!");
17       }
18     };
19     
20     // Lambda Runnable
21     Runnable r2 = () -> System.out.println("Hello world two!");
22     
23     // Run em!
24     r1.run();
25     r2.run();
26     
27   }
28 }
```





### 对于集合使用Comparator

```java
10 public class ComparatorTest {
11 
12   public static void main(String[] args) {
13    
14     List<Person> personList = Person.createShortList();
15   
16     // Sort with Inner Class
17     Collections.sort(personList, new Comparator<Person>(){
18       public int compare(Person p1, Person p2){
19         return p1.getSurName().compareTo(p2.getSurName());
20       }
21     });
22     
23     System.out.println("=== Sorted Asc SurName ===");
24     for(Person p:personList){
25       p.printName();
26     }
27     
28     // Use Lambda instead
29     
30     // Print Asc
31     System.out.println("=== Sorted Asc SurName ===");
32     Collections.sort(personList, (Person p1, Person p2) -> p1.getSurName().compareTo(p2.getSurName()));
33 
34     for(Person p:personList){
35       p.printName();
36     }
37     
38     // Print Desc
39     System.out.println("=== Sorted Desc SurName ===");
40     Collections.sort(personList, (p1,  p2) -> p2.getSurName().compareTo(p1.getSurName()));
41 
42     for(Person p:personList){
43       p.printName();
44     }
45   }
46 }
```

注意可以指定类型也可以不指定：`(Person p1,Person p2)`    or  `(p1,p2)`