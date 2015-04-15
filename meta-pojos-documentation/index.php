<?php
include "../header_mp.php"
?>

<html lang="fr">
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<!--<link rel="shortcut icon" href="../../assets/ico/favicon.png">-->

<title>Meta Pojos</title>

<!-- Bootstrap core CSS -->
<link
	href="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css"
	rel="stylesheet">
<!-- Custom styles for this template -->
<link href="starter-template.css" rel="stylesheet">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
		<script src="lib/bootstrap-3.2.0-dist/js/html5shiv.js"></script>
		<script src="lib/bootstrap-3.2.0-dist/js/respond.min.js"></script>
		<![endif]-->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js">
</script>
<script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
</head>


<body style="background-color: rgba(255, 255, 255, 1);padding:10px">
<div class="page-header">
<h1 class="nocount">Meta Pojos</h1>
</div>


<h1>What is it ?</h1>
<p>Meta Pojos is an <b>eclipse plugin</b> hat allows you to <b>browse your Java code</b>.<br>
The querying is done in Java, allowing you to use your favorite IDE for content assist. For example, to get all calls to all methods named <code>getXXX</code> in classes whose name contains <code>DAO</code> you can do this :</p>
<pre class="prettyprint lang-java">MetaPojos.getClasses("DAO").getMethods("get").getCallsTo().print();</pre>
<p>The results appear in a special console that has hyperlinks to places in your code :</p>
<p><img alt="" src="screenshot-console.gif"></p>
<p>The above is the result from a query for methods in <code>ArrayList</code> containing <code>add</code>.</p>


<h1>Open source</h1>
Meta Pojos is open source. You fill find the source code and instructions about how to install from source code on github : <a href="https://github.com/yannicklerestif/meta-pojos"> https://github.com/yannicklerestif/meta-pojos</a>


<h1>Getting started</h1>
<h2>Requirements</h2>
Meta Pojos has the following requirements :
<ul>
<li>Meta Pojos makes a heavy use of streams, so it needs Java 8 as the eclipse JVM.</li>
<li>Depending on the size of the projects in your workspace, you might need to have some available memory in your eclipse
 JVM. Meta Pojos will use about 5KB per class, which is 500MB for a project with 100,000 classes.</li>   
</ul>

<h2>Download and install</h2>
In eclipse, click <i>Help / Install new software...</i>, Click <i>Add...</i>, and add the following update site :<br/>
<a href="http://yannicklerestif.com/meta-pojos/p2">http://yannicklerestif.com/meta-pojos/p2</a>.<br>
Follow the instructions, then restart eclipse.

<h2>Running the sample query</h2>
<p>Click <i>File / New / Other...</i>, and choose to create a <i>Meta Pojos</i> project :</p>
<p><img alt="" src="screenshot-new.gif"></p>
<p>If Meta Pojos category is not displayed, it most probably means eclipse couldn't install it, and a probable cause for this is your eclipse JVM is not Java 8.</p>
<p>In the new window, choose a project name, for example <i>meta-pojos-query</i>, and click <i>Finish</i>.<br>
<b>Make sure you use a Java 8 JVM.</b></p>
<p>In the new project, navigate to the class <code>src/query/MetaPojosQuery.java</code>, and open it. Note the <i>mp</i> decorator, that indicates that the project is a Meta Pojos project :<p>
<p><img alt="" src="screenshot-mp-project.gif"></p>
<p>Right click <code>MetaPojosQuery.java</code>, and choose <i>Run As / Meta Pojos Query.</i></p>
<p>Meta Pojos reads the classes in your workspace, then executes the query, and displays the results in its console.
The results have hyperlinks to places in your code.</p>
<p>Note that Meta Pojos will not read classes again until it has to, which means that if you want to execute another query,
 it will yield results instantly. Besides, once files are cached on your hard disk, loading can be significatively faster.
 All in all, for a 100,000 classes workspace, classes should usually be read in about 10 seconds.</p>  

<h2>The sample class</h2>
<pre class="prettyprint lang-java">
package query;

import com.yannicklerestif.metapojos.MetaPojos;

public class MetaPojosQuery {
    public static void main(String[] args) throws Exception {         
        MetaPojos.getClasses("java.util.ArrayList")
            .getMethods("add").print();                              //(1)
        MetaPojos.getConsole().println("----------------------");    //(2)   
        MetaPojos.getConsole().println("Classes analyzed : "  
            + MetaPojos.getClasses().stream().count());              //(3)
    }
}
</pre>

<h3>A simple query (1)</h3>
<p>Most things you will do with Meta Pojos start by calling a static method on the class <code>com.yannicklerestif.metapojos.MetaPojos</code>.</p>
<p>Here we use the method <code>getClasses(String)</code>, that gets all the classes whose names contains the argument string.<br/>
The result is a <code>com.yannicklerestif.metapojos.model.elements.streams.ClassStream</code> object.
This and the other Meta Pojos streams (now <code>MethodStream</code> and <code>CallStream</code>) are the next most 
important classes in Meta Pojos.<br></p>
<p>Here, we use the method <code>getMethods(String)</code>, that returns all methods whose names contain the argument string.<br>
The result is a <code>MethodStream</code>.</p>
<p>The last method we call is the method <code>print()</code>. It is available for all Meta Pojos streams, and outputs the result as a console hyperlink.<br>
Actually, you could virtually have do anything with the result. But whatever you do, you must do it explicitly, so <b>don't forget to call <code>print()</code></b> if that's what you want to do !</p>
 
<h3>MetaPojos console (2)</h3>
You can have access to Meta Pojos console using <code>MetaPojos.getConsole()</code>.
Feel free to log anything you want here. Just don't use <code>System.out.println()</code>, because this will write in eclipse' log, not in the console ! <br/>
See below why Meta Pojos queries run in eclipse JVM.

<h3>Accessing the underlying stream (3)</h3>
<p>On each Meta Pojos Java elements streams (<code>ClassStream</code>, <code>MethodStream</code>, <code>CallStream</code>), can can have access to the underlying
Java 8 stream by calling the method <code>stream()</code>. Using this, you can do virtually anything with the results : filtering, joining, ...
but this is beyond the scope of this short introduction. Here we just count the results to output the number of analyzed classes to the console.</p>


<h1>Interested ?</h1>
<p>I hope this will be enough to get you started.</p>
<p>If you have questions, I will be happy to answer them. You will find my email address on github 
(see "Open Source" above). Just ask the question on Stack Overflow first (or elsewhere), so that others can
benefit from it, and then notify me.</p>
<p>If you have suggestions (or maybe want to contribute), you can do so on github using their issues and pull request facilities.
There is already a lot of things that can easily be done. I have used "TODOs" in the code for this.</p>
<p>In any case, I'll be happy to hear from you !</p>

</body>  