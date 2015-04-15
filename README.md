# meta-pojos
## What is it ?
Meta Pojos is an eclipse plugin hat allows you to browse your Java code.
The querying is done in Java, allowing you to use your favorite IDE for content assist. For example, to get all calls to all methods named `getXXX` in classes whose name contains `DAO` you can do this :
```java
mp.allClasses().filter(clazz -> (clazz.getName().contains("DAO")).getMethods().getCallsTo();
```
The results appear in a special console that has hyperlinks to places in your code :
![console screenshot](http://yannicklerestif.com/meta-pojos/screenshot-console.gif)
The above is the result from a query for methods in `ArrayList` containing `add`.
