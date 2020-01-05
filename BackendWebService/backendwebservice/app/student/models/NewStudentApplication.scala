package student.models

import play.api.libs.json._
import university.models.UniversityId

case class NewStudentApplication(studentApplicationId: StudentApplicationId,
  universityId: UniversityId, version: StudentApplicationVersion)
object NewStudentApplication {
  def apply(studentApplication: StudentApplication): NewStudentApplication = {
    NewStudentApplication(studentApplication.id,
      studentApplication.universityId,
      studentApplication.version)
  }

  implicit val formatNewStudentApplication: Format[NewStudentApplication] =
    Json.format[NewStudentApplication]
}
