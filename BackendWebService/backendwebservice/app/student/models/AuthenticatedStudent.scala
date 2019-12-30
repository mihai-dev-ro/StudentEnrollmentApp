package student.models

import authentication.models.SecurityUserId
import student.models

case class AuthenticatedStudent(studentId: StudentId, securityUserId: SecurityUserId, token: String)

object AuthenticatedStudent {

  def apply(studentAndToken: (Student, String)): AuthenticatedStudent = {
    val (student, token) = studentAndToken
    models.AuthenticatedStudent(student.id, student.securityUserId, token)
  }
}