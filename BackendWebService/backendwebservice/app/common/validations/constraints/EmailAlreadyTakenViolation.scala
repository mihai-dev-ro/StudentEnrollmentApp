package common.validations.constraints

import common.models.Email

case class EmailAlreadyTakenViolation(email: Email) extends Violation {
  override def message: String = s"email: ${email.value} este deja inregistrat in sistem"
}
