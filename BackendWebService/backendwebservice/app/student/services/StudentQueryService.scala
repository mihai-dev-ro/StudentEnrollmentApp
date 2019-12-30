package student.services

import authentication.models.SecurityUserId
import common.models.Email
import javax.inject.Inject
import slick.dbio.DBIO
import student.models.{Student, StudentId}
import student.repositories.StudentRepo

import scala.concurrent.ExecutionContext

class StudentQueryService (studentRepo: StudentRepo)(
  implicit private val ex: ExecutionContext)  {

  def getStudent(id: StudentId): DBIO[Student] = {
    require(id != null)

    studentRepo.findById(id)
  }

  def getStudent(email: Email): DBIO[Student] = {
    require(email != null)

    studentRepo.findByEmail(email)
  }

  def getStudentBySecurityUserId(securityUserId: SecurityUserId): DBIO[Student] = {
    require(securityUserId != null)

    studentRepo.findBySecurityUserId(securityUserId)
  }
}
