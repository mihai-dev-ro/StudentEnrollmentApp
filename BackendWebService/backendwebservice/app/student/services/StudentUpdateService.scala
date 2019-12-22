package student.services

import common.exceptions.ValidationException
import common.services.DbActionRunner
import common.utils.DBIOUtils
import javax.inject.Inject
import slick.dbio.DBIO
import student.models.{Student, StudentForUpdating, StudentId}
import student.repositories.StudentRepo

import scala.concurrent.ExecutionContext

// service class that is handles the persistence of state into the DB
class StudentUpdateService @Inject()(studentRepo: StudentRepo,
                                     studentUpdateValidator: StudentUpdateValidator)(
                                    implicit private val ec: ExecutionContext) {

  def update(studentId: StudentId, studentDetails: StudentForUpdating): DBIO[Student] = {
    // validate the user
    for {
      student <- studentRepo.findById(studentId)
      _ <- validate(student, studentDetails)
      
    }
    // get the user from db

    // update the user


  }

  def validate(student: Student, studentDetails: StudentForUpdating) = {
    studentUpdateValidator.validate(student, studentDetails)
      .flatMap(violations => DBIOUtils.fail(violations.isEmpty,
        new ValidationException(violations)))
  }
}
