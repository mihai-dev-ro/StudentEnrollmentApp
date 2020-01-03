package university.repository

import common.models.{IdMetaModel, Property}
import common.repositories.{BaseRepo, IdTable}
import slick.dbio.DBIO
import slick.lifted._
import university.models._
import slick.jdbc.H2Profile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

import scala.concurrent.ExecutionContext

case class UniversityIdWithDNSDomain(id: UniversityId, dnsDomain: UniversityDNSDomain)

class UniversityDNSDomainRepo(implicit val ec: ExecutionContext)
  extends BaseRepo[UniversityDNSDomainId, UniversityDNSDomain, UniversityDNSDomainTable] {

  def getAllWithGroupingInfo(): DBIO[Seq[UniversityIdWithDNSDomain]] = {
    query
      .map(table => (table.universityId, table))
      .result
      .map(_.map((UniversityIdWithDNSDomain.apply _).tupled))
  }

  def getByUniversityId(universityId: UniversityId): DBIO[Seq[UniversityDNSDomain]] = {
    require(universityId != null)

    query
      .filter(_.universityId === universityId)
      .result
  }

  override protected val mappingConstructor: Tag => UniversityDNSDomainTable =
    new UniversityDNSDomainTable(_)

  override implicit protected val modelIdMapping: BaseColumnType[UniversityDNSDomainId] =
    UniversityDNSDomainId.universityDNSDomainIdDbMapping

  override protected val metaModelToColumnsMapping: Map[Property[_], UniversityDNSDomainTable => Rep[_]] =
    Map(UniversityDNSDomainMetaModel.id -> (table => table.id),
      UniversityDNSDomainMetaModel.name -> (table => table.name))

  override protected val metaModel: IdMetaModel = UniversityDNSDomainMetaModel
}

class UniversityDNSDomainTable(tag: Tag)
  extends IdTable[UniversityDNSDomainId, UniversityDNSDomain](tag, "University_DNS_domains") {

  def universityId: Rep[UniversityId] = column[UniversityId]("University_id")
  def name: Rep[String] = column[String]("Name")

  def * : ProvenShape[UniversityDNSDomain] =
    (id, universityId, name) <> ((UniversityDNSDomain.apply _).tupled,
      UniversityDNSDomain.unapply)
}