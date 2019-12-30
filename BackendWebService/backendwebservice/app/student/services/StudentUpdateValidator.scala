package student.services

import authentication.models.PlainTextPassword
import common.models.Email
import common.validations.PropertyViolation
import slick.dbio.DBIO
import student.models.{Student, StudentForUpdating}

import scala.concurrent.ExecutionContext

private[student] class StudentUpdateValidator(emailValidator: EmailValidator,
                                              passwordValidator: PasswordValidator)(
  implicit private val ec: ExecutionContext) {

  def validate(student: Student, studentForUpdating: StudentForUpdating) = {
    for {
      emailViolations <- validateEmail(student, studentForUpdating)
      passwordViolations =  validatePassword(student, studentForUpdating)
    } yield emailViolations ++ passwordViolations
  }

  def validateEmail(student: Student, studentForUpdating: StudentForUpdating) = {
    studentForUpdating.email
        .filter(_ != student.email)
        .map(emailValidator.validate(_))
        .getOrElse(DBIO.successful(Seq.empty))
        .map(violations => violations.map(violation => PropertyViolation("Email", violation)))
  }

  def validatePassword(student: Student, studentForUpdating: StudentForUpdating) = {
    studentForUpdating.password
      .map(passwordValidator.validate(_))
      .getOrElse(Seq.empty)
      .map(violation => PropertyViolation("Password", violation))
  }
}
