package student.models

import play.api.libs.json.{Format, Json}
import university.models.UniversityId

case class StudentApplicationStartWrapper(
  universityName: String, universityCountryCode: String)
object StudentApplicationStartWrapper {
  implicit val formatStudentApplicationStartWrapper: Format[StudentApplicationStartWrapper] =
    Json.format[StudentApplicationStartWrapper]
}
