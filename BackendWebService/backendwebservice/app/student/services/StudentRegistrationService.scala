package student.services

import authentication.api.SecurityUserCreator
import common.exceptions.ValidationException
import common.repositories.DateTimeProvider
import common.utils.DBIOUtils
import authentication.models.{NewSecurityUser}
import student.models.{Student, StudentId, StudentRegistration}
import student.repositories.StudentRepo
import play.api.Configuration
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext.Implicits.global

private[student] class StudentRegistrationService(studentRegistrationValidator: StudentRegistrationValidator,
                                             securityUserCreator: SecurityUserCreator,
                                             dateTimeProvider: DateTimeProvider,
                                             studentRepo: StudentRepo,
                                             config: Configuration) {
  
  def register(studentRegistration: StudentRegistration): DBIO[Student] = {
    for {
      _ <- validate(studentRegistration)
      user <- doRegister(studentRegistration)
    } yield user
  }

  private def validate(studentRegistration: StudentRegistration) = {
    studentRegistrationValidator.validate(studentRegistration)
      .flatMap(violations =>
        DBIOUtils.fail(violations.isEmpty, new ValidationException(violations)))
  }

  private def doRegister(studentRegistration: StudentRegistration) = {
    val newSecurityUser = NewSecurityUser(studentRegistration.email, studentRegistration.password)
    for {
      securityUser <- securityUserCreator.create(newSecurityUser)
      now = dateTimeProvider.now
      student = Student(StudentId(-1), securityUser.id, studentRegistration.email,
        null, null, now, now)
      savedStudent <- studentRepo.insertAndGet(student)
    } yield savedStudent
  }
}



