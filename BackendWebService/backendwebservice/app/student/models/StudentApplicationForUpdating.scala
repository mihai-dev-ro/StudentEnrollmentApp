package student.models

import play.api.libs.json.{Format, Json}
import university.models.UniversityId

case class StudentApplicationForUpdating(
  academicRecords: Option[String],
  honorsAndDistinctions: Option[String],
  volunteerActivities: Option[String],
  otherInterests: Option[String],
  coverLetter: Option[String])

object StudentApplicationForUpdating {
  implicit val formatStudentApplicationForUpdating: Format[StudentApplicationForUpdating] =
    Json.format[StudentApplicationForUpdating]
}
