# meta-pojos
The purpose of meta-pojos is to provide a very intuitive and easy-to-use api to browse your Java code.
The querying is done in Java, allowing you to use your favorite IDE for content assist.
The results are output to console. If you use eclipse the output will be hyperlinks to places in your code.
## Setup
Once you have downloaded the project, import meta-pojos-main into your IDE. It is a maven project.  
I don't know if you'll find interest in the other project, meta-pojos-test-classes : is a just a sample project to browse. Browsing your own projects should be more interesting !
```
meta-pojos
|-meta-pojos-main
|-meta-pojos-test-classes
```
Then, have a look at the class meta-pojos-main/src/test/java/com/yannicklerestif/metapojos/MetaPojosTest.
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

## How does it work
The These methods return a `ClassStream` object, 
