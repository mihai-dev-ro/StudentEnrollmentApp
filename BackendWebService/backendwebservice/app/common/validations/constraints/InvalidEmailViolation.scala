package common.validations.constraints

import common.models.Email

case class InvalidEmailViolation(email: Email) extends Violation {
  override def message: String = s"email: ${email.value} este incorect"
}
