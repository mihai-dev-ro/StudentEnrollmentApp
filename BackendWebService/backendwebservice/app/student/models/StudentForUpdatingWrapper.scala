package student.models

import play.api.libs.json.{Format, Json}

private[student] case class StudentForUpdatingWrapper(student: StudentForUpdating)
object StudentForUpdatingWrapper {
  implicit val formatStudentForUpdatingWrapper: Format[StudentForUpdatingWrapper] =
    Json.format[StudentForUpdatingWrapper]
}
