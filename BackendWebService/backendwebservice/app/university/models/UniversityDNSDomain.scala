package university.models

import common.models.{BaseId, IdMetaModel, Property, WithId}
import play.api.libs.json.{Format, JsResult, JsValue, Json, Reads, Writes}
import slick.jdbc.H2Profile.api._

case class UniversityDNSDomainId(override val value: Long) extends AnyVal with BaseId[Long]
object UniversityDNSDomainId {
  implicit val universityDNSDomainIdFormat: Format[UniversityDNSDomainId] =
    new Format[UniversityDNSDomainId] {

    override def writes(o: UniversityDNSDomainId): JsValue =
      Writes.LongWrites.writes(o.value)

    override def reads(json: JsValue): JsResult[UniversityDNSDomainId] =
      Reads.LongReads.reads(json).map(UniversityDNSDomainId(_))
  }

  implicit val universityDNSDomainIdDbMapping: BaseColumnType[UniversityDNSDomainId] =
    MappedColumnType.base[UniversityDNSDomainId, Long](
      vo => vo.value,
      id => UniversityDNSDomainId(id)
    )
}

case class UniversityDNSDomain(
  id: UniversityDNSDomainId,
  universityId: UniversityId,
  name: String,
) extends WithId[Long, UniversityDNSDomainId]

object UniversityDNSDomainMetaModel extends IdMetaModel {
  override type ModelId = UniversityDNSDomainId

  val universityId: Property[UniversityId] = Property("University_id")
  val name: Property[String] = Property("Name")
}

