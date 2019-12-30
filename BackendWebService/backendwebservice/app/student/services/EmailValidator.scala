package student.services

import authentication.api.SecurityUserProvider
import slick.dbio.{DBIO}
import common.models._
import common.validations.constraints._

import scala.concurrent.ExecutionContext

private[student] class EmailValidator(securityUserProvider: SecurityUserProvider)(
                     implicit private val ec: ExecutionContext) {

  private val emailValidator = org.apache.commons.validator.routines.EmailValidator.getInstance()

  def validate(email: Email): DBIO[Seq[Violation]] = {
    if (email == null) DBIO.successful(Seq(NotNullViolation))
    else if (isNotEmailValid(email)) DBIO.successful(Seq(InvalidEmailViolation(email)))
    else validateEmailAlreadyTaken(email)
  }

  def validateEmailAlreadyTaken(email: Email) = {
    securityUserProvider.findByEmailOption(email)
      .map(maybeSecurityUser => {
        if (maybeSecurityUser.isDefined) Seq(EmailAlreadyTakenViolation(email))
        else Nil
      })
  }

  def isNotEmailValid(email: Email) = {
    !emailValidator.isValid(email.value)
  }
}
