package student.services

import authentication.api.SecurityUserUpdater
import authentication.models.{SecurityUser, SecurityUserToUpdate}
import common.exceptions.ValidationException
import common.repositories.DateTimeProvider
import common.utils.DBIOUtils
import slick.dbio.DBIO
import student.models.{Student, StudentForUpdating, StudentId}
import student.repositories.StudentRepo

import scala.concurrent.ExecutionContext

// service class that is handles the persistence of state into the DB
class StudentUpdateService (studentRepo: StudentRepo,
  securityUserUpdater: SecurityUserUpdater,
  studentUpdateValidator: StudentUpdateValidator,
  dateTimeProvider: DateTimeProvider)(
  implicit private val ec: ExecutionContext) {

  def update(studentId: StudentId, studentDetails: StudentForUpdating): DBIO[Student] = {
    // validate the user
    for {
      student <- studentRepo.findById(studentId)
      _ <- validate(student, studentDetails)
      studentUpdated <- updateStudent(student, studentDetails)
      _ <- updateSecurityUser(student, studentDetails)
    } yield studentUpdated
  }

  def validate(student: Student, studentDetails: StudentForUpdating) = {
    studentUpdateValidator.validate(student, studentDetails)
      .flatMap(violations => DBIOUtils.fail(violations.isEmpty,
        new ValidationException(violations)))
  }

  def updateStudent(student: Student, studentDetails: StudentForUpdating): DBIO[Student] = {
    val studentToUpdate = student.copy(email = studentDetails.email.getOrElse(student.email),
      name = studentDetails.name,
      education = studentDetails.education,
      updatedAt = dateTimeProvider.now
    )

    studentRepo.updateAndGet(studentToUpdate)
  }

  def updateSecurityUser(student: Student, studentDetails: StudentForUpdating):
    DBIO[SecurityUser] = {

    securityUserUpdater.update(student.securityUserId,
      SecurityUserToUpdate(email = studentDetails.email, studentDetails.password))
  }
}
