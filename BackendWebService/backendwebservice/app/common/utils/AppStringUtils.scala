package common.utils

object AppStringUtils {
  def startsWithWhiteSpace(str: String): Boolean = str.matches("(?U)^\\s.*")

  def endsWithWhiteSpace(str: String): Boolean = str.matches("(?U).*\\s$")

  def countWords(str: String): Int = {
    str.split("\\s+|,\\s*|\\.\\s*").map(_ => 1).reduce(_ + _)
  }
}
