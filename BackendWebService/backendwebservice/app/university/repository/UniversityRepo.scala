package university.repository

import scala.collection.immutable._
import common.models.{IdMetaModel, Ordering, Property}
import common.repositories.{BaseRepo, IdTable}
import slick.dbio.DBIO
import slick.lifted._
import university.models._
import slick.jdbc.H2Profile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

import scala.concurrent.ExecutionContext

class UniversityRepo(dnsDomainRepo: UniversityDNSDomainRepo,
  websiteRepo: UniversityWebsiteRepo)(
  implicit val ec: ExecutionContext
) extends BaseRepo[UniversityId, University, UniversityTable] {

  def getAllWithCompleteInfo(): DBIO[Seq[UniversityWithCompleteInfo]] = {
    for {
      universities <- findAll
      universityIdWithDNSDomains <- getGroupDNSDomainsByUniversityIds()
      universityIdWithWebsites <- getGroupedWebsitesByUniversityIds()
    } yield createUniversityWithCompleteInfo(universities, universityIdWithDNSDomains,
      universityIdWithWebsites)
  }

  def getByIdWithCompleteInfo(id: UniversityId): DBIO[UniversityWithCompleteInfo] = {
    require(id != null)

    for {
      university <- findById(id)
      dnsDomainsForUniversity <- dnsDomainRepo.getByUniversityId(id)
      websitesForUniversity <- websiteRepo.getByUniversityId(id)
    } yield UniversityWithCompleteInfo(university, dnsDomainsForUniversity, websitesForUniversity)
  }

  def getByIdWithCompleteInfoOption(id: UniversityId): DBIO[Option[UniversityWithCompleteInfo]] = {
    require(id != null)

    val unknownUniversity = University(UniversityId(-1), "", "", "")

    val results = for {
      university <- findByIdOption(id)
      universityId = university.getOrElse(unknownUniversity).id
      dnsDomainsForUniversity <- dnsDomainRepo.getByUniversityId(universityId)
      websitesForUniversity <- websiteRepo.getByUniversityId(id)
    } yield (university, dnsDomainsForUniversity, websitesForUniversity)

    results.map(universityAndAll => universityAndAll._1 match {
      case Some(university) => Some(UniversityWithCompleteInfo.apply(university, universityAndAll._2,
        universityAndAll._3))
      case None => None
    })
  }

  def getByNameAndCountryCodeWithCompleteInfoOption(name: String, countryCode: String) = {
    require(name != null && countryCode != null)

    val unknownUniversity = University(UniversityId(-1), "", "", "")

    val results = for {
      university <- findByNameAndCountryCodeOption(name, countryCode)
      universityId = university.getOrElse(unknownUniversity).id
      dnsDomainsForUniversity <- dnsDomainRepo.getByUniversityId(universityId)
      websitesForUniversity <- websiteRepo.getByUniversityId(universityId)
    } yield (university, dnsDomainsForUniversity, websitesForUniversity)

    results.map(universityAndAll => universityAndAll._1 match {
      case Some(university) => Some(UniversityWithCompleteInfo.apply(university, universityAndAll._2,
        universityAndAll._3))
      case None => None
    })
  }

  def findByNameAndCountryCodeOption(name: String, countryCode: String) = {
    query
      .filter(_.name === name)
      .filter(_.countryCode === countryCode)
      .result
      .headOption
  }

  def getGroupDNSDomainsByUniversityIds() = {
    dnsDomainRepo.getAllWithGroupingInfo()
      .map(_.groupBy(model => model.id))
  }

  def getGroupedWebsitesByUniversityIds() = {
    websiteRepo.getAllWithGroupingInfo()
      .map(_.groupBy(model => model.id))
  }

  def createUniversityWithCompleteInfo(universities: Seq[University],
    universityIdWithDNSDomains: Map[UniversityId, Seq[UniversityIdWithDNSDomain]],
    universityIdWithWebsites: Map[UniversityId, Seq[UniversityIdWithWebsite]]
  ) = {

    def createHelper(university: University) = {
      val dnsDomains = universityIdWithDNSDomains.getOrElse(university.id, Seq.empty)
        .map(_.dnsDomain)
      val websites = universityIdWithWebsites.getOrElse(university.id, Seq.empty)
          .map(_.website)

      UniversityWithCompleteInfo(university, dnsDomains, websites)
    }

    universities.map(createHelper)
  }

  override protected val mappingConstructor: Tag => UniversityTable = new UniversityTable(_)

  override implicit protected val modelIdMapping: BaseColumnType[UniversityId] =
    UniversityId.universityIdDbMapping

  override protected val metaModelToColumnsMapping: Map[Property[_], UniversityTable => Rep[_]] =
    Map(UniversityMetaModel.id -> (table => table.id),
      UniversityMetaModel.name -> (table => table.name),
      UniversityMetaModel.countryName -> (table => table.countryName))

  override protected val metaModel: IdMetaModel = UniversityMetaModel
}

class UniversityTable(tag: Tag)
  extends IdTable[UniversityId, University](tag, "Universities") {

  def name: Rep[String] = column[String]("Name")
  def countryCode: Rep[String] = column[String]("Country_code")
  def countryName: Rep[String] = column[String]("Country")

  def * : ProvenShape[University] =
    (id, name, countryCode, countryName) <> ((University.apply _).tupled,
      University.unapply)
}