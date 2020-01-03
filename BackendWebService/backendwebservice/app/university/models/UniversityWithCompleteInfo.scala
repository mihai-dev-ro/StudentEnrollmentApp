package university.models

import play.api.libs.json.{Format, Json}

case class UniversityWithCompleteInfo(
  id: UniversityId,
  name: String,
  countyCode: String,
  countryName: String,
  dnsDomains: Seq[String],
  websites: Seq[String]
)

object UniversityWithCompleteInfo {
  implicit val universityWithCompleteInfoFormat: Format[UniversityWithCompleteInfo] =
    Json.format[UniversityWithCompleteInfo]

  def apply(university: University, universityDnsDomains: Seq[UniversityDNSDomain],
    universityWebsites: Seq[UniversityWebsite]): UniversityWithCompleteInfo = {

    val dnsDomains = universityDnsDomains.map(_.name).sorted
    val websites = universityWebsites.map(_.url).sorted

    UniversityWithCompleteInfo(
      university.id,
      university.name,
      university.countyCode,
      university.countryName,
      dnsDomains,
      websites
    )
  }
}
