package student.models

import java.time.Instant

import authentication.models.JwtToken
import common.models.Email
import play.api.libs.json.{Format, Json}

case class StudentDetailsWithToken(email: Email, name: Option[String], education: Option[String],
                                   createdAt: Instant, updatedAt: Instant,
                                   token: String)

object StudentDetailsWithToken {
  def apply(student: Student, token: String): StudentDetailsWithToken =
    new StudentDetailsWithToken(student.email, student.name, student.education,
      student.createdAt, student.updatedAt, token)

  implicit val formatStudentDetailsWithToken: Format[StudentDetailsWithToken] =
    Json.format[StudentDetailsWithToken]
}

case class StudentDetailsWithTokenWrapper(student: StudentDetailsWithToken)
object StudentDetailsWithTokenWrapper {
  implicit val formatStudentDetailsWithTokenWrapper: Format[StudentDetailsWithTokenWrapper] =
    Json.format[StudentDetailsWithTokenWrapper]
}
