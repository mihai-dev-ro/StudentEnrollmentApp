package university.services

import common.exceptions.MissingModelException
import play.api.libs.json._
import play.api.libs.ws.WSClient
import university.models.{UniversityId, UniversityWithCompleteInfo}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class UniversityQueryExternalProvider(wsClient: WSClient)(
  implicit ec: ExecutionContext) {
  self =>

  private val universityAPIUrl = "http://universities.hipolabs.com/search"

  private val responseUnitDecoder: Reads[UniversityWithCompleteInfo] = new Reads[UniversityWithCompleteInfo] {
    override def reads(json: JsValue): JsResult[UniversityWithCompleteInfo] = {

      val name = (json \ "name").as[String]
      val countryCode = (json \ "alpha_two_code").as[String]
      val countryName = (json \ "country").as[String]
      val dnsDomains = Reads.JsArrayReads.reads((json \ "domains").get)
        .map(_.value.map(_.validate[String].get))
        .map(_.toSeq).get
      val websites = Reads.JsArrayReads.reads((json \ "web_pages").get)
        .map(_.value.map(_.validate[String].get))
        .map(_.toSeq).get

      val university = UniversityWithCompleteInfo(
        UniversityId(-1), name, countryCode, countryName, dnsDomains, websites)

      JsSuccess(university)
    }
  }

  private val responseSeqDecoder: Reads[Seq[UniversityWithCompleteInfo]] =
    new Reads[Seq[UniversityWithCompleteInfo]] {

    override def reads(json: JsValue): JsResult[Seq[UniversityWithCompleteInfo]] = {
      Reads.JsArrayReads.reads(json)
        .map(_.value.map(_.validate[UniversityWithCompleteInfo](self.responseUnitDecoder).get))
        .map(_.zipWithIndex.map(universityAndIndex =>
          universityAndIndex._1.copy(id = UniversityId(universityAndIndex._2 + 1))))
        .map(_.toSeq)
    }
  }

  def getAllUniversitiesWithCompleteInfo(): Future[Either[RuntimeException, Seq[UniversityWithCompleteInfo]]] = {
    val universityRequest = wsClient.url(universityAPIUrl)
      .addHttpHeaders("Accept" -> "application/json")
      .withRequestTimeout(10000.millis)

    universityRequest.get()
      .map(_.json.validate[Seq[UniversityWithCompleteInfo]](self.responseSeqDecoder).get)
      .map(Right(_))
      .recover({
        case e: scala.concurrent.TimeoutException =>
          Left(new RuntimeException(e.toString()))
      })
  }

  def getUniversityWithCompleteInfo(universityId: UniversityId) = {
    getAllUniversitiesWithCompleteInfo()
      .map(_.filterOrElse(_.map(_.id == universityId).size > 0,
        new MissingModelException("universitatea nu a fost gasita")))
      .map(_.map(_.filter(_.id == universityId).head))
      .flatMap(_ match {
        case Left(e) => Future.failed(e)
        case Right(university) => Future.successful(university)
      })
  }

  def getUniversityWithCompleteInfo(name: String, countryCode: String) = {
    getAllUniversitiesWithCompleteInfo()
      .map(_.filterOrElse(_.filter(
        u => u.name.equalsIgnoreCase(name) && u.countyCode.equalsIgnoreCase(countryCode)).size > 0,
        new MissingModelException("universitatea nu a fost gasita")))
      .map(_.map(_.filter(
        u => u.name.equalsIgnoreCase(name) && u.countyCode.equalsIgnoreCase(countryCode)).head))
      .flatMap(_ match {
        case Left(e) => Future.failed(e)
        case Right(university) => Future.successful(university)
      })
  }
}
