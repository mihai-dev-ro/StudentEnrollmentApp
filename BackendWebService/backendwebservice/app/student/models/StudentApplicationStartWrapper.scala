package student.models

import play.api.libs.json.{Format, Json}
import university.models.UniversityId

case class StudentApplicationStartWrapper(universityId: UniversityId)
object StudentApplicationStartWrapper {
  implicit val formatStudentApplicationStartWrapper: Format[StudentApplicationStartWrapper] =
    Json.format[StudentApplicationStartWrapper]
}
