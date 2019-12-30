package student.models

import authentication.models.PlainTextPassword
import common.models.{Email}
import play.api.libs.json.{Format, Json}

private[student] case class StudentRegistration(password: PlainTextPassword,
                                                email: Email)

object StudentRegistration {
  implicit val userRegistrationFormat: Format[StudentRegistration] =
    Json.format[StudentRegistration]
}