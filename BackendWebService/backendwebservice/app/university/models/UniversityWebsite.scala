package university.models

import common.models.{BaseId, IdMetaModel, Property, WithId}
import play.api.libs.json.{Format, JsResult, JsValue, Json, Reads, Writes}
import slick.jdbc.H2Profile.api._

case class UniversityWebsiteId(override val value: Long) extends AnyVal with BaseId[Long]
object UniversityWebsiteId {
  implicit val universityWebsiteIdFormat: Format[UniversityWebsiteId] =
    new Format[UniversityWebsiteId] {

      override def writes(o: UniversityWebsiteId): JsValue =
        Writes.LongWrites.writes(o.value)

      override def reads(json: JsValue): JsResult[UniversityWebsiteId] =
        Reads.LongReads.reads(json).map(UniversityWebsiteId(_))
    }

  implicit val universityWebsiteIdDbMapping: BaseColumnType[UniversityWebsiteId] =
    MappedColumnType.base[UniversityWebsiteId, Long](
      vo => vo.value,
      id => UniversityWebsiteId(id)
    )
}

case class UniversityWebsite(
  id: UniversityWebsiteId,
  universityId: UniversityId,
  url: String,
) extends WithId[Long, UniversityWebsiteId]

object UniversityWebsiteMetaModel extends IdMetaModel {
  override type ModelId = UniversityWebsiteId

  val universityId: Property[UniversityId] = Property("University_id")
  val url: Property[String] = Property("Url")
}

