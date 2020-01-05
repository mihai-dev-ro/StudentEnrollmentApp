package student.models

import play.api.libs.json.{Format, Json}

case class StudentApplicationFile(attachedFileUrl: Option[String])
object StudentApplicationFile {
  implicit val formatStudentApplicationFile: Format[StudentApplicationFile] =
    Json.format[StudentApplicationFile]
}
