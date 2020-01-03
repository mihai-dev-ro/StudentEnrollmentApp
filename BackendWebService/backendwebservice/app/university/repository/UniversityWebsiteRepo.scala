package university.repository

import common.models.{IdMetaModel, Property}
import common.repositories.{BaseRepo, IdTable}
import slick.dbio.DBIO
import slick.lifted._
import university.models._
import slick.jdbc.H2Profile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

import scala.concurrent.ExecutionContext

case class UniversityIdWithWebsite(id: UniversityId, website: UniversityWebsite)

class UniversityWebsiteRepo(implicit val ec: ExecutionContext)
  extends BaseRepo[UniversityWebsiteId, UniversityWebsite, UniversityWebsiteTable] {

  def getAllWithGroupingInfo(): DBIO[Seq[UniversityIdWithWebsite]] = {
    query
      .map(table => (table.universityId, table))
      .result
      .map(_.map((UniversityIdWithWebsite.apply _).tupled))
  }

  def getByUniversityId(universityId: UniversityId): DBIO[Seq[UniversityWebsite]] = {
    require(universityId != null)

    query
      .filter(_.universityId === universityId)
      .result
  }

  override protected val mappingConstructor: Tag => UniversityWebsiteTable =
    new UniversityWebsiteTable(_)

  override implicit protected val modelIdMapping: BaseColumnType[UniversityWebsiteId] =
    UniversityWebsiteId.universityWebsiteIdDbMapping

  override protected val metaModelToColumnsMapping: Map[Property[_], UniversityWebsiteTable => Rep[_]] =
    Map(UniversityWebsiteMetaModel.id -> (table => table.id),
      UniversityWebsiteMetaModel.url -> (table => table.url))

  override protected val metaModel: IdMetaModel = UniversityWebsiteMetaModel
}

class UniversityWebsiteTable(tag: Tag)
  extends IdTable[UniversityWebsiteId, UniversityWebsite](tag, "University_websites") {

  def universityId: Rep[UniversityId] = column[UniversityId]("University_id")
  def url: Rep[String] = column[String]("Url")

  def * : ProvenShape[UniversityWebsite] =
    (id, universityId, url) <> ((UniversityWebsite.apply _).tupled,
      UniversityWebsite.unapply)
}