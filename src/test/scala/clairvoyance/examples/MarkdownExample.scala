package clairvoyance.examples

import org.specs2.clairvoyance.ClairvoyantSpec
import org.specs2.specification.Snippets

class MarkdownExample extends ClairvoyantSpec with Snippets {

  "The Calculator" should {
    """Calculate the factorial of 3
      |### Rules
      |* The factorial of n is the product of all positive integers less than or equal to n
      |* *Non-negative* integers only
      |* [Futher reading](http://en.wikipedia.org/wiki/Factorial)
    """.stripMargin in {
      3.factorial == 6
    }
  }

  implicit class IntFactorial(n: Int) {
    def factorial = Calculator.factorial(n)
  }

  object Calculator {
    def factorial(n: Int): Int =
      if (n == 1) n
      else n * factorial(n - 1)

  }
}
