package university.models

import common.models.{BaseId, IdMetaModel, Property, WithId}
import play.api.libs.json._
import slick.jdbc.H2Profile.api._

case class UniversityId(override val value: Long)
  extends AnyVal with BaseId[Long]

object UniversityId {
  implicit val universityIdFormat: Format[UniversityId] =
    new Format[UniversityId]{

    override def reads(json: JsValue): JsResult[UniversityId] =
      Reads.IntReads.reads(json).map(UniversityId(_))

    override def writes(o: UniversityId): JsNumber =
      Writes.LongWrites.writes(o.value)
  }

  implicit val universityIdDbMapping: BaseColumnType[UniversityId] =
    MappedColumnType.base[UniversityId, Long] (

    vo => vo.value,
    id => UniversityId(id)
  )
}

case class University(id: UniversityId,
  name: String,
  countyCode: String,
  countryName: String
) extends WithId[Long, UniversityId]

object University {
  implicit val universityFormat: Format[University] = Json.format[University]
}

object UniversityMetaModel extends IdMetaModel {
  val name: Property[String] = Property("Name")
  val countryCode: Property[String] = Property("Country_code")
  val countryName: Property[String] = Property("Country")

  override type ModelId = UniversityId
}




