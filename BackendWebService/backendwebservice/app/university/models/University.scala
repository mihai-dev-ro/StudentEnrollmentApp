package university.models

import common.models.{BaseId, IdMetaModel, Property, WithId}
import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._

case class UniversityId(override val value: Int)
  extends AnyVal
    with BaseId[Int]

object UniversityId {
  implicit val universityIdFormat: Format[UniversityId] =
    new Format[UniversityId]{

    override def reads(json: JsValue): JsResult[UniversityId] =
      Reads.IntReads.reads(json).map(UniversityId(_))

    override def writes(o: UniversityId): JsNumber =
      Writes.IntWrites.writes(o.value)
  }

  implicit val universityIdDbMapping: BaseColumnType[UniversityId] =
    MappedColumnType.base[UniversityId, Int] (

    vo => vo.value,
    id => UniversityId(id)
  )
}

case class University(id: UniversityId,
                      name: String,
                      codeCountry: String,
                      country: String,
                      stateProvince: String
                     ) extends WithId[Int, UniversityId]

object University {
  implicit val universityFormat: Format[University] = Json.format[University]
}

object UniversityMetaModel extends IdMetaModel {
  val name: Property[String] = Property("name")
  val codeCountry: Property[String] = Property("codeCountry")
  val country: Property[String] = Property("country")
  val stateProvince: Property[String] = Property("stateProvince")

  override type ModelId = UniversityId
}




