package student.models

import authentication.models.PlainTextPassword
import common.models.Email
import play.api.libs.json.{Format, Json}

case class StudentForUpdating(email: Option[Email], name: Option[String], education: Option[String],
                              password: Option[PlainTextPassword])

object StudentForUpdating {
  implicit val formatStudentForUpdating: Format[StudentForUpdating] =
    Json.format[StudentForUpdating]
}
