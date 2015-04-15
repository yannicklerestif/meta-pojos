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

## User documentation

Meta Pojos home page is located at [http://yannicklerestif.com/meta-pojos.com](http://yannicklerestif.com/meta-pojos.com).
There you will find some detailed instructions about how to install it and use it.

## Build from source
Pre-requisite : of course your eclipse must have PDE installed. But it must also run on a Java 8 JVM, because Meta Pojos is very much based on Java 8 streams.
Clone or fork meta-pojos, then import root project and sub-projects in eclipse (*Import / Existing Projects into Workspace*).
The main folders under the root are :
```
meta-pojos
|-meta-pojos-plugin
| |-src-main
| |-src-plugin
| |-dist
| |-plugin.xml
|-meta-pojos-samples
|-meta-pojos-update-site
| |-site.xml
|-build.xml
```
Building from source is nothing special, apart from one thing : on first launch, and each time you modify the api, you must run an ant task that re-builds Meta Pojos sample project dependencies (This should be easy to improve, but that's how it works right now :)). The ant task is `meta-pojos/build.xml/dist_for_sample_project`.

This is enough to run another eclipse that has the plugin compiled from source. To do that, right-click `meta-pojos-plugin` and choose *Run As / Eclipse Application*.

To build the whole project site, double-click `meta-pojos/meta-pojos-update-site/site.xml` and open the site `meta-pojos`. Clicking *Build All* is supposed to be enough, but I found out I have to remove the feature (right-click and then *Remove*) and add it again, being careful to choose the one with the version 1.1.0.qualifier, not the one with the qualifier resolved (e.g. 1.1.0.201504151448). Then, click *Build All*. You can then use the output as a local update site for any eclipse application (using *Help/Install New Software...* for a new installation, or *Help/Installation Details* for updating).
