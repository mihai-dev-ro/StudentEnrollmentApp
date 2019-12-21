package common.utils

object AppStringUtils {
  def startsWithWhiteSpace(str: String): Boolean = str.matches("(?U)^\\s.*")

  def endsWithWhiteSpace(str: String): Boolean = str.matches("(?U).*\\s$")
}
