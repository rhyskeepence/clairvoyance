package clairvoyance.scalatest.examples

import org.scalatest.Spec

class MarkdownExampleWithSpec extends Spec {

  object `The Triangle Number Calculator` {
    markup {
      """###Rules
        |* The nth triangle number is the number of dots composing a triangle with n dots on a side, and is equal to the sum of the n natural numbers from 1 to n
        |* [Further reading](http://en.wikipedia.org/wiki/Triangular_number)
        |
        |Here we calculate the 6th triangle number, represented by the following diagram:
        |```
        |       *
        |      **
        |     ***
        |    ****
        |   *****
        |  ******
        |```
        |""".stripMargin
    }
    def `can calculate the sixth triangle number`() {
      triangle(6) == 21
    }
  }

  object `The Calculator` {
    markup {
      """### Rules
        |* The factorial of n is the product of all positive integers less than or equal to n
        |* *Non-negative* integers only
        |* [Further reading](http://en.wikipedia.org/wiki/Factorial)
      """.stripMargin
    }
    def `can calculate the factorial of 3`() {
      factorial(3) == 6
    }
  }

  def factorial(n: Int): Int = if (n == 1) n else n * factorial(n - 1)
  def triangle (n: Int): Int = n * (n + 1) / 2
}
