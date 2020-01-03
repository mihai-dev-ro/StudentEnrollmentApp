package student.models

import java.time.Instant

import authentication.models.SecurityUserId
import common.models.{BaseId, Email, IdMetaModel, Property, WithId}
import play.api.libs.json._
import slick.jdbc.H2Profile.api._

case class StudentId(override val value: Long) extends AnyVal with BaseId[Long]
object StudentId {
  implicit val StudentIdFormat: Format[StudentId] = new Format[StudentId] {
    override def writes(o: StudentId): JsValue = Writes.LongWrites.writes(o.value)

    override def reads(json: JsValue): JsResult[StudentId] =
      Reads.IntReads.reads(json).map(StudentId(_))
  }

  implicit val studentIdDbMapping: BaseColumnType[StudentId] =
    MappedColumnType.base[StudentId, Long] (vo => vo.value, id => StudentId(id))

}

case class Student(id: StudentId,
                   securityUserId: SecurityUserId,
                   email: Email,
                   name: Option[String],
                   education: Option[String],
                   createdAt: Instant,
                   updatedAt: Instant) extends WithId[Long, StudentId]

object StudentMetaModel extends IdMetaModel {
  override type ModelId = StudentId

  val email: Property[String] = Property("Email")
  val name: Property[String] = Property("Name")
  val education: Property[String] = Property("Education")
}


