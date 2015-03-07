# meta-pojos
The purpose of meta-pojos is to provide a very intuitive and easy-to-use API to browse your Java code.
The querying is done in Java, allowing you to use your favorite IDE for content assist. For example you can do this :
```java
mp.allClasses().filter(clazz -> (clazz.getName().contains("DAO")).getMethods().getCallsTo();
```
This will give you all calls to all methods in classes whose name contains "DAO".
The results are output to console. If you use eclipse the output will be hyperlinks to places in your code.

The project is inspired by the very nice [Browse By Query project](http://browsebyquery.sourceforge.net/).   
Have a look at the documentation there if you're wondering what could be done with meta-pojos, because everything you can do with BBQ should be do-able with meta-pojos, it's just a matter of adding methods to the API. So if you're interested but there's something you think I should add, don't hesitate to tell me. 

The project should also become an eclipse plugin, so that you don't have to tell it where your classes are, and also because it will be nicer to display the results as a tree in a specific view (that would look like search results or call hierarchy).

## Setup
Once you have downloaded the project, import meta-pojos-main into your IDE. It is a maven project.  
I don't know if you'll find interest in the other project, meta-pojos-test-classes : is a just a sample project to browse. Browsing your own projects should be more interesting !
```
meta-pojos
|-meta-pojos-main
|-meta-pojos-test-classes
```
Then, have a look at the class [meta-pojos-main/src/test/java/com/yannicklerestif/metapojos/MetaPojosTest.java](./meta-pojos-main/src/test/java/com/yannicklerestif/metapojos/MetaPojosTest.java).  
It should work as is, you only need to change the directory where you put meta-pojos.

## Launching
You init MetaPojos like this :
```java
MetaPojos mp = MetaPojos.start();
mp.readClasses("/home/yannick/Projets/meta-pojos/meta-pojos-test-classes/bin");
```
you can use class directories, jars, or both :
```
mp.readClasses("myDir1", "myJar1.jar", "myDir2");
```
Use only one `readClasses` instruction.  
Classes directories must be in **binary** format (.class files, not source files).

## Querying
A query will then look like this :
```java
mp.singleClass("test.model.hierarchy.MyInterface").getMethods().getCallsTo().print();
```
There are two starting points :
- `mp.singleClass(String className)` (or add your project as a dependency and use `com.yourcompany.YourClass.class.getName()`)
- `mp.allClasses()`

From here, you can just use content assist to see what you can do, or read ahead for some more explanations :

### How does it work
The two methods above return a `ClassStream` object, that wraps a Java 8 `Stream<ClassBean>`, `ClassBean` being 
the class that represents a Java class.  
On `ClassStream` you can for example apply `getMethods()`. This will return a `MethodStream`, wrapping a stream of all the methods in the classes in the classes stream.  
Then, from the methods stream, you can get all the calls from these methods, or all the calls **to** these methods... and so on.  
And, because object streams wrap streams, you can also use some stream methods, like `filter`, `foreach`, merge them, and so on.
Last but not least, because it's Java code you can really do whatever you want : use custom inputs, write results to wherever you like, perform some processing on intermediary results, debug your code... 

### The output
Output will look like the following. On eclipse hyperlinks are clickable.

Call from test.model.StartingClass.startingMethod([StartingClass.java:9]()) to test.model.hierarchy.MyInterfaceImplChild.\<init>([MyInterfaceImplChild.java:3]())  
Call from test.model.StartingClass.startingMethod([StartingClass.java:10]()) to test.model.hierarchy.MyInterface.myInterfaceMethod([MyInterface.java:-1]())   
Call from test.model.StartingClass.\<init>([StartingClass.java:6]()) to java.lang.Object.\<init>([Object.java:-1]())  
Call from test.model.hierarchy.MyInterfaceImplParent.\<init>([MyInterfaceImplParent.java:3]()) to java.lang.Object.\<init>([Object.java:-1]())  
Call from test.model.SomeClass.doSomething([SomeClass.java:5]()) to java.lang.Object.\<init>([Object.java:-1]())  
Call from test.model.SomeClass.doSomething([SomeClass.java:6]()) to java.lang.Object.equals([Object.java:-1]())  
Call from test.model.SomeClass.\<init>([SomeClass.java:3]()) to java.lang.Object.\<init>([Object.java:-1]())  
Call from test.model.hierarchy.MyInterfaceImpl.\<init>([MyInterfaceImpl.java:3]()) to test.model.hierarchy.MyInterfaceImplParent.\<init>([MyInterfaceImplParent.java:3]())  
