package university

import com.softwaremill.macwire._
import authentication.AuthenticationComponents
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSClient
import play.api.routing.Router
import play.api.routing.sird._
import student.controllers.authentication_middleware.AuthenticatedActionBuilder
import university.controllers.UniversityController
import university.repository.{UniversityDNSDomainRepo, UniversityRepo, UniversityWebsiteRepo}
import university.services.{UniversityQueryExternalProvider, UniversityQueryProvider}

trait UniversityComponents
  extends AuthenticationComponents {

  def wsClient: WSClient
  def jwtAuthenticatedActionBuilder: AuthenticatedActionBuilder

  // wire the objects
  lazy val universityDNSDomainRepo: UniversityDNSDomainRepo = wire[UniversityDNSDomainRepo]
  lazy val universityWebsiteRepo: UniversityWebsiteRepo = wire[UniversityWebsiteRepo]
  lazy val universityRepo: UniversityRepo = wire[UniversityRepo]
  lazy val universityQueryProvider: UniversityQueryProvider = wire[UniversityQueryProvider]
  lazy val universityQueryExternalProvider: UniversityQueryExternalProvider =
    wire[UniversityQueryExternalProvider]
  lazy val universityController: UniversityController = wire[UniversityController]

  // routes
  val universityRoutes: Router.Routes = {
    case GET(p"/universities") =>
      universityController.getAllFromPublicAPI
  }
}
