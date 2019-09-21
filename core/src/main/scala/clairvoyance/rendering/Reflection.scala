package clairvoyance.rendering

import java.lang.reflect.Constructor
import scala.reflect.ClassTag
import scala.util.Properties.lineSeparator

/** Inlined from org.specs2.reflect.Classes */
object Reflection {
  /**
   * Try to create an instance of a given class by using whatever constructor is available
   * and trying to instantiate the first parameter recursively if there is a parameter for that constructor.
   *
   * This is useful to instantiate nested classes which are referencing their outer class in their constructor
   */
  def tryToCreateObject[T <: AnyRef](className: String,
                                     loader: ClassLoader = getClass.getClassLoader,
                                     printMessage: Boolean = false,
                                     printStackTrace: Boolean = false)
                                    (implicit m: ClassTag[T]): Option[T] =
    tryToCreateObjectEither(className, loader) match {
      case Right(o) => Some(o)
      case Left(e)  =>
        if (printMessage) println(e.getMessage)
        if (printStackTrace) println(e.getStackTrace.mkString("", lineSeparator, lineSeparator))
        None
    }

  private def tryToCreateObjectEither[T <: AnyRef](className: String, loader: ClassLoader)
                                                  (implicit m: ClassTag[T]): Either[Throwable, T] = {
    loadClassEither(className, loader) match {
      case Left(e) => Left(e)
      case Right(c: Class[_]) =>
        try {
          val constructors = c.getDeclaredConstructors.toList.filter(_.getParameterTypes.size <= 1).sortBy(_.getParameterTypes.size)
          if (constructors.isEmpty)
            Left(new Exception("Can't find a constructor for class " + c.getName))
          else {
            val results = constructors.map(constructor => createInstanceForConstructor(c, constructor, None))
            results.find(_.isRight) match {
              case Some(r) => r
              case None =>
                val exception = results.collect {
                  case Left(e) => e
                }.iterator.toSeq.headOption.getOrElse(new Exception("no cause"))
                Left(new Exception("Could not instantiate class " + c.getName + ": " + exception.getMessage, exception))
            }
          }
        } catch {
          case e: Throwable => Left(new Exception("Could not instantiate class " + className + ": " + e.getMessage, e))
        }
    }
  }

  /**
   * Load a class, given the class name
   *
   * If the 'debugLoadClass' property is set, then an error message is printed out to the Console
   */
  private def loadClassEither[T <: AnyRef](className: String, loader: ClassLoader): Either[Throwable, Class[T]] =
    tryEither(loadClassOf(className, loader).asInstanceOf[Class[T]]) { case e =>
      printError(className, loader, e)
      e
    }

  /**
   * Given a class, a zero or one-parameter constructor, return an instance of that class
   */
  private def createInstanceForConstructor[T <: AnyRef : ClassTag](c: Class[_], constructor: Constructor[_],
                                                                   parameter: Option[AnyRef]): Either[Throwable, T] = {
    if (constructor.getParameterTypes.isEmpty)
      createInstanceOfEither[T](Some[Class[T]](c.asInstanceOf[Class[T]]))
    else if (constructor.getParameterTypes.size == 1) {
      // if the specification has a construction, it is either because it is a nested class
      // or if it has an Arguments parameter
      // or it might have a parameter that has a 0 args constructor
      val outerClass = tryToCreateObject[T](getOuterClassName(c))
      val constructorParameter =
        outerClass.
          orElse(parameter).
          orElse(tryToCreateObject[AnyRef](constructor.getParameterTypes.toSeq(0).getName))

      constructorParameter.map(constructor.newInstance(_).asInstanceOf[T]).toRight {
        new Exception("can't create an instance of "+c+" for a constructor with parameter "+constructorParameter)
      }
    } else Left(new Exception("Can't find a suitable constructor for class " + c.getName))
  }

  /**
   * @return an instance of a given class, checking that the created instance type-checks as expected
   */
  private def createInstanceOfEither[T <: AnyRef](c: Option[Class[T]])(implicit m: ClassTag[T]): Either[Throwable, T] =
    try c.map(createInstanceFor(_)).toRight(new Exception())
    catch { case e: Throwable => Left(e) }

  /**
   * @return an instance of a given class, checking that the created instance type-checks as expected
   */
  private def createInstanceFor[T <: AnyRef](klass: Class[T])(implicit m: ClassTag[T]): T = {
    val constructor = klass.getDeclaredConstructors()(0)
    constructor.setAccessible(true)
    val instance: AnyRef = constructor.newInstance().asInstanceOf[AnyRef]
    if (!m.runtimeClass.isInstance(instance)) sys.error(instance + " is not an instance of " + m.runtimeClass.getName)
    instance.asInstanceOf[T]
  }

  /**
   * @return the outer class name for a given class
   */
  private def getOuterClassName(c: Class[_]): String = c.getDeclaredConstructors.toList(0).getParameterTypes.toList(0).getName

  /**
   * try to evaluate an expression, returning Either
   *
   * If the expression throws an Exception a function f is used to return the left value
   * of the Either returned value.
   */
  private def tryEither[T, S](a: =>T)(implicit f: Exception => S): Either[S, T] =
    try Right(a) catch { case e: Exception => Left(f(e)) }

  /**
   * Load a class, given the class name, without catching exceptions
   */
  private def loadClassOf[T <: AnyRef](className: String, loader: ClassLoader): Class[T] =
    loader.loadClass(className).asInstanceOf[Class[T]]

  private def printError(className: String, loader: ClassLoader, e: Throwable): Unit = {
    if (sys.props("debugLoadClass") != null) {
      println("loader is "+loader)
      println("Could not load class " + className + ": " + e.getMessage)
      e.getStackTrace foreach (s => println(s.toString))
    }
  }
}
