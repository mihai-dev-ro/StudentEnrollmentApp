package university

import common.config.WithExecutionContext
import play.api.routing.Router
import play.api.routing.sird._
import university.controllers.UniversityController

trait UniversityComponents
  extends WithExecutionContext {

  val universityController: UniversityController = new UniversityController()

  val universityRoutes: Router.Routes = {
    case GET(p"/universities") =>
      universityController.getAll
  }
}
