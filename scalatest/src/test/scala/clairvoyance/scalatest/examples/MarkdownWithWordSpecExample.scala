package clairvoyance.scalatest.examples

import org.scalatest.WordSpec

class MarkdownWithWordSpecExample extends WordSpec {

  """The Triangle Number Calculator
    |###Rules
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
    | """.stripMargin can {
    "calculate the sixth triangle number" in {
      triangle(6) == 21
    }
  }

  """The Calculator
    |### Rules
    |* The factorial of n is the product of all positive integers less than or equal to n
    |* *Non-negative* integers only
    |* [Further reading](http://en.wikipedia.org/wiki/Factorial)
  """.stripMargin can {
    "calculate the factorial of 3" in {
      factorial(3) == 6
    }
  }

  def factorial(n: Int): Int = if (n == 1) n else n * factorial(n - 1)
  def triangle(n: Int): Int  = n * (n + 1) / 2
}
