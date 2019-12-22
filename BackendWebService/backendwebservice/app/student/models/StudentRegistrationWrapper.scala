package student.models

import play.api.libs.json.{Format, Json}

private[student] case class StudentRegistrationWrapper(student: StudentRegistration)

object StudentRegistrationWrapper {
  implicit val formatStudentRegistrationWrapper: Format[StudentRegistrationWrapper] =
    Json.format[StudentRegistrationWrapper]
}
