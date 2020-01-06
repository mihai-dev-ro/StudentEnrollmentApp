package university.services

import university.models._
import university.repository._

import scala.concurrent.ExecutionContext

class UniversityUpdateService(universityRepo: UniversityRepo,
  universityDNSDomainRepo: UniversityDNSDomainRepo,
  universityWebsiteRepo: UniversityWebsiteRepo)(
  implicit ec: ExecutionContext
) {

  def addUniversity(universityWithCompleteInfo: UniversityWithCompleteInfo) = {

    val universityToInsert = University(
      id = UniversityId(-1),
      name = universityWithCompleteInfo.name,
      countyCode = universityWithCompleteInfo.countyCode,
      countryName = universityWithCompleteInfo.countryName
    )

    val universityDNSDomainsToInsert = universityWithCompleteInfo.dnsDomains.map(domain => {
      UniversityDNSDomain(
        UniversityDNSDomainId(-1),
        universityId = UniversityId(-1),
        name = domain
      )
    })

    val universityWebsitesToInsert = universityWithCompleteInfo.websites.map(websiteUrl => {
      UniversityWebsite(
        UniversityWebsiteId(-1),
        universityId = UniversityId(-1),
        url = websiteUrl
      )
    })

    for {
      university <- universityRepo.insertAndGet(universityToInsert)
      universityDNSDomains <- associateUniversityWithDomains(university, universityDNSDomainsToInsert)
      universityWebsites <- associateUniversityWithWebsites(university, universityWebsitesToInsert)
    } yield UniversityWithCompleteInfo(university, universityDNSDomains, universityWebsites)
  }

  def associateUniversityWithDomains(university: University,
    universityDNSDomains: Seq[UniversityDNSDomain]) = {

    val domainsLinked = universityDNSDomains.map(_.copy(universityId = university.id.copy()))
    universityDNSDomainRepo.insertAndGet(domainsLinked)
  }

  def associateUniversityWithWebsites(university: University,
    universityWebsites: Seq[UniversityWebsite]) = {

    val websitesLinked = universityWebsites.map(_.copy(universityId = university.id.copy()))
    universityWebsiteRepo.insertAndGet(websitesLinked)
  }
}
