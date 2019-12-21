package student.services

import authentication.models.PlainTextPassword
import common.utils.AppStringUtils
import common.validations.constraints._

private[student] class PasswordValidator {
  private val minPassLength = 8
  private val maxPassLength = 255

  def validate(password: PlainTextPassword): Seq[Violation] = {
    if (password == null) Seq(NotNullViolation)
    else {
      val rawPassword = password.value

      if (rawPassword.length < minPassLength) Seq(MinLengthViolation(minPassLength))
      else if (rawPassword.length > maxPassLength) Seq(MaxLengthViolation(maxPassLength))
      else if (AppStringUtils.startsWithWhiteSpace(rawPassword)
        || AppStringUtils.endsWithWhiteSpace(rawPassword)) Seq(PrefixOrSuffixWithWhiteSpacesViolation)
      else Nil
    }
  }
}
