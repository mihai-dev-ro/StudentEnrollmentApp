package student.authentication

import authentication.models.SecurityUserId
import student.models.{Student, StudentId}

case class AuthenticatedStudent(studentId: StudentId, securityUserId: SecurityUserId, token: String)

object AuthenticatedStudent {

  def apply(studentAndToken: (Student, String)): AuthenticatedStudent = {
    val (student, token) = studentAndToken
    AuthenticatedStudent(student.id, student.securityUserId, token)
  }
}