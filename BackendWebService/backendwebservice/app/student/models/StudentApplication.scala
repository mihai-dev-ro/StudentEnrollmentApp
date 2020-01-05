package student.models

import java.time.Instant

import common.models._
import play.api.libs.json.{Format, JsResult, JsValue, Json, Reads, Writes}
import slick.jdbc.H2Profile.api._
import university.models.UniversityId

object StudentApplicationStatus extends Enumeration {
  type StudentApplicationStatus = Value

  val Draft = Value(1)
  val Submitted = Value(2)

  implicit val studentApplicationStatusFormat: Format[StudentApplicationStatus] =
    Json.formatEnum(StudentApplicationStatus)
}


case class StudentApplicationId(override val value: Long) extends AnyVal with BaseId[Long]
object StudentApplicationId {
  implicit val studentApplicationIdFormat: Format[StudentApplicationId] =
    new Format[StudentApplicationId] {

    override def writes(o: StudentApplicationId): JsValue = Writes.LongWrites.writes(o.value)

    override def reads(json: JsValue): JsResult[StudentApplicationId] = Reads.LongReads.reads(json)
      .map(StudentApplicationId(_))
  }

  implicit val studentApplicationIdDbMapping: BaseColumnType[StudentApplicationId] =
    MappedColumnType.base[StudentApplicationId, Long](
      vo => vo.value,
      id => StudentApplicationId(id)
    )
}

case class StudentApplicationVersion(value: Int) extends AnyVal
object StudentApplicationVersion {
  implicit val studentApplicationVersionFormat: Format[StudentApplicationVersion] =
    new Format[StudentApplicationVersion] {

      override def writes(o: StudentApplicationVersion): JsValue = Writes.IntWrites.writes(o.value)

      override def reads(json: JsValue): JsResult[StudentApplicationVersion] = Reads.IntReads.reads(json)
        .map(StudentApplicationVersion(_))
    }

  implicit val studentApplicationVersionDbMapping: BaseColumnType[StudentApplicationVersion] =
    MappedColumnType.base[StudentApplicationVersion, Int](
      vo => vo.value,
      v => StudentApplicationVersion(v)
    )
}

case class StudentApplication(id: StudentApplicationId,
  version: StudentApplicationVersion,
  studentId: StudentId,
  universityId: UniversityId,
  status: StudentApplicationStatus.Value,
  academicRecords: Option[String],
  honorsAndDistinctions: Option[String],
  volunteerActivities: Option[String],
  otherInterests: Option[String],
  coverLetter: Option[String],
  attachedFileUrl: Option[String],
  createdAt: Instant,
  updatedAt: Instant
) extends WithId[Long, StudentApplicationId]

object StudentApplication {
  implicit val studentApplicationFormat: Format[StudentApplication] =
    Json.format[StudentApplication]
}

object StudentApplicationMetaModel extends IdMetaModel {
  override type ModelId = StudentApplicationId

  val version: Property[StudentApplicationVersion] =
    new Property[StudentApplicationVersion]("Version")
  val studentId: Property[StudentId] = new Property[StudentId]("Student_Id")
  val universityId: Property[UniversityId] = new Property[UniversityId]("University_Id")
  val status: Property[StudentApplicationStatus.Value] =
    new Property[StudentApplicationStatus.Value]("Status")

  val academicRecords: Property[Option[String]] = new Property[Option[String]]("Academic_records")
  val honorsAndDistinctions: Property[Option[String]] = new Property[Option[String]]("Honors_and_distinctions")
  val volunteerActivities: Property[Option[String]] = new Property[Option[String]]("Volunteer_activities")
  val otherInterests: Property[Option[String]] = new Property[Option[String]]("Other_interests")
  val coverLetter: Property[Option[String]] = new Property[Option[String]]("Cover_letter")
  val attachedFileUrl: Property[Option[String]] = new Property[Option[String]]("Attached_file_url")
}
