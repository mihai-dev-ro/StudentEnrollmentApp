package student.services

import common.services.DbActionRunner
import common.validations.PropertyViolation
import student.models.StudentRegistration
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

private[student] class StudentRegistrationValidator(passwordValidator: PasswordValidator,
                                               emailValidator: EmailValidator,
                                               actionRunner: DbActionRunner)(
                                               implicit private val ex: ExecutionContext) {

  def validate(studentRegistration: StudentRegistration): DBIO[Seq[PropertyViolation]] = {
    for {
      emailViolations <- validateEmail(studentRegistration)
      passwordViolations = validatePassword(studentRegistration)
    } yield passwordViolations ++ emailViolations
  }

  private def validatePassword(studentRegistration: StudentRegistration) = {
    passwordValidator.validate(studentRegistration.password)
      .map(violation => PropertyViolation("Password", violation))
  }

  private def validateEmail(studentRegistration: StudentRegistration) = {
    emailValidator.validate(studentRegistration.email)
      .map(violations => violations.map(violation => PropertyViolation("Email", violation)))
  }
}



