package university

import com.softwaremill.macwire._
import authentication.AuthenticationComponents
import play.api.routing.Router
import play.api.routing.sird._
import university.controllers.UniversityController

trait UniversityComponents
  extends AuthenticationComponents {

  val universityController: UniversityController = wire[UniversityController]

  val universityRoutes: Router.Routes = {
    case GET(p"/universities") =>
      universityController.getAll
  }
}
