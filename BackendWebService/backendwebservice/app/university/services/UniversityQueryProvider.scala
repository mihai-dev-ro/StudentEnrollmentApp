package university.services

import slick.dbio.DBIO
import university.models.{UniversityId, UniversityWithCompleteInfo}
import university.repository.UniversityRepo

import scala.concurrent.ExecutionContext

class UniversityQueryProvider(universityRepo: UniversityRepo)(
  implicit val ec: ExecutionContext) {

  def getAllUniversitiesWithCompleteInfo: DBIO[Seq[UniversityWithCompleteInfo]] = {
    universityRepo.getAllWithCompleteInfo()
  }

  def getUniversityWithCompleteInfo(id: UniversityId): DBIO[UniversityWithCompleteInfo] = {
    require(id != null)

    universityRepo.getByIdWithCompleteInfo(id)
  }
}
