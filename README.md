Clairvoyance
============

Clairvoyance is an extension to Specs2, a Scala test library. Clairvoyance provides a few extensions to capture what is
happening in your tests, and then produce business and tester friendly documentation.

It's a pastiche of [Yatspec](http://code.google.com/p/yatspec), a Java testing library written by my colleague Dan
Bodart. It addresses the deficiencies we experienced with Fit and Concordion.

[![Build Status](https://secure.travis-ci.org/rhyskeepence/clairvoyance.svg)](http://travis-ci.org/rhyskeepence/clairvoyance)

Example
-------

The full source to this example is [here](https://github.com/rhyskeepence/clairvoyance/blob/master/src/test/scala/clairvoyance/specs2/examples/LoggingExample.scala).

It breaks down like this:

* Create a Spec which extends `ClairvoyantSpec`
* Write the spec in the mutable spec style (for various historical reasons)
* Create a context which extends `ClairvoyantContext`
* `InterestingGivens` can be added with statements such as `interestingGivens += ("Current date" -> "21/12/2012")`
* The Scala code within the spec method is interpreted into a text specification, to encourage readability.

```scala
class LoggingExample extends ClairvoyantSpec {
  "The coordinator" should {
    "invoke the Doomsday Device on the 21st of December 2012" in new context {
      givenTheDateIs("21/12/2012")
      whenTheCoordinatorRuns
      theDoomsdayDevice should beUnleashed
    }
  }

  trait context extends ClairvoyantContext {
    // test set up and fixtures
  }
}
```

Here is the output of this spec.
![Example output](http://github.com/rhyskeepence/clairvoyance/raw/master/doc/example-output.jpg)

Get This Party Started
----------------------

Add this to your SBT build:

    libraryDependencies ++= Seq(
        "com.github.rhyskeepence" %% "clairvoyance" % "1.0.<latest travis build number>"
        //  or, for scala 2.9.2: 
        //  "com.github.rhyskeepence" %% "clairvoyance" % "27"
    )
    resolvers ++= Seq(
        "releases"  at "http://oss.sonatype.org/content/repositories/releases"
    )

Or in Maven:

```xml
    <dependency>
        <groupId>com.github.rhyskeepence</groupId>
        <artifactId>clairvoyance_2.10.0</artifactId>
        <version>1.0.latest travis build number</version>
        <scope>test</scope>
    </dependency>

    ....

    <repository>
        <id>sonatype-releases</id>
        <name>sonatype releases</name>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <url>http://oss.sonatype.org/content/repositories/releases</url>
    </repository>
```

Interesting Givens
------------------

These are inputs into your test, which may not be specified in the spec, but should be logged to the output:

```scala
  interestingGivens += ("Current date" -> "21/12/2012")
```

or

```scala
  ("Current date" -> "21/12/2012").isInteresting
```


Captured Inputs And Outputs
---------------------------

These are the inputs or outputs to your system, which may not be practical to assert upon, but should be logged.

Perhaps you are using a stub rather than communicating with a third party in your spec:

```scala
class StubGizmometer extends Gizmometer {
}
```

To capture inputs and outputs, just add the `ProducesCapturedInputsAndOutputs` trait and call `captureValue`:

```scala
class StubGizmometer extends Gizmometer with ProducesCapturedInputsAndOutputs {
  def scan(brain: Brain) {
    captureValue("Brain" -> brain)
  }
}
```

and in your context, register the stub so that clairvoyant knows about it:

```scala
trait context extends ClairvoyantContext {
    val gizmometer = new StubGizmometer
    override def capturedInputsAndOutputs = Seq(gizmometer)
}
```

Custom Rendering of Interesting Givens & Captured Inputs And Outputs
--------------------------------------------------------------------

When you capture a value or an interesting given, it will be rendered to the screen. XML and Strings are formatted
nicely by default, but you may wish to capture your own domain objects and have them presented in readable format.

A full example is here: [clairvoyance/specs2/examples/CustomRenderingExample.scala](https://github.com/rhyskeepence/clairvoyance/blob/master/src/test/scala/clairvoyance/specs2/examples/CustomRenderingExample.scala)

The juicy bits are shown below:

```scala
class CustomRenderingExample extends ClairvoyantSpec with CustomRendering {

  def customRendering = {
    case Brain(iq) => "a Brain with an IQ of %d".format(iq)
  }
}
```

`customRendering` is a partial function, which will be run before the default rendering.

And behold, custom rendering of Brains:

![Custom Rendering of Brains](http://github.com/rhyskeepence/clairvoyance/raw/master/doc/custom-rendering.jpg)

Sequence Diagrams
-----------------

If your spec describes interactions between many systems, it can be nice to generate a sequence diagram automatically
from CapturedInputsAndOutputs. Just add the `SequenceDiagram` trait to your context, ie:

```scala
trait context extends ClairvoyantContext with SequenceDiagram {
  override def capturedInputsAndOutputs = Seq(system_x, system_y)
}
```

The name of the captured values should be in the following formats in order to appear on the diagram:

`captureValue("SOMETHING from X to Y" -> ...)` or

`captureValue("SOMETHING from X" -> ...)` or

`captureValue("SOMETHING to Y" -> ...)`

In the last two cases, the default actor will be used, which can be set using this statement in the context:
`override def defaultSequenceDiagramActor = "Name of my component"`

An example can be found [here](https://github.com/rhyskeepence/clairvoyance/blob/master/src/test/scala/clairvoyance/specs2/examples/SequenceDiagramExample.scala),
which produces the following output:

![Sequence Diagram](http://github.com/rhyskeepence/clairvoyance/raw/master/doc/sequence.jpg)

Alternatively, a graph can be produced:

```scala
trait context extends ClairvoyantContext with Graph
```

Markdown
--------

[Markdown](http://en.wikipedia.org/wiki/Markdown) is supported in specification descriptions, to whet your appetite
[see this example](https://github.com/rhyskeepence/clairvoyance/blob/master/src/test/scala/clairvoyance/specs2/examples/MarkdownExample.scala).

OTHER COOL STUFF!!!
-------------------

* [ScalaCheck](https://github.com/rhyskeepence/clairvoyance/blob/master/src/test/scala/clairvoyance/specs2/examples/ScalaCheckExample.scala)
* [Graphs](https://github.com/rhyskeepence/clairvoyance/blob/master/src/test/scala/clairvoyance/specs2/examples/GraphExample.scala)
* [G/W/T](https://github.com/rhyskeepence/clairvoyance/blob/master/src/test/scala/clairvoyance/specs2/examples/GivenWhenThenExample.scala)

TODO
----

* Scenario tables
