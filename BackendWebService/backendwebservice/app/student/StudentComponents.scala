package student

import com.softwaremill.macwire._
import authentication.AuthenticationComponents
import play.api.mvc.PlayBodyParsers
import play.api.routing.Router
import play.api.routing.sird._
import student.controllers.authentication_middleware.AuthenticatedActionBuilder
import student.controllers.authentication_middleware.jwt.JwtAuthenticatedActionBuilder
import student.controllers.{LoginController, StudentController}
import student.repositories.StudentRepo
import student.services._

trait StudentComponents
  extends AuthenticationComponents {

  def playBodyParsers: PlayBodyParsers

  //repo
  lazy val studentRepo: StudentRepo = wire[StudentRepo]

  // utilities
  lazy val passwordValidator: PasswordValidator = wire[PasswordValidator]
  lazy val emailValidator: EmailValidator = wire[EmailValidator]
  lazy val studentRegistrationValidator = wire[StudentRegistrationValidator]
  lazy val studentUpdateValidator: StudentUpdateValidator = wire[StudentUpdateValidator]

  // services
  lazy val studentRegistrationService: StudentRegistrationService = wire[StudentRegistrationService]
  lazy val studentQueryService: StudentQueryService = wire[StudentQueryService]
  lazy val studentUpdateService: StudentUpdateService = wire[StudentUpdateService]

  // custom actions
  lazy val jwtAuthenticatedActionBuilder: AuthenticatedActionBuilder =
    wire[JwtAuthenticatedActionBuilder]

  // controllers
  lazy val loginController: LoginController = wire[LoginController]
  lazy val studentController: StudentController = wire[StudentController]

  // routes
  val studentRoutes: Router.Routes = {
    case POST(p"/users/login") => loginController.login

    case POST(p"/users") => studentController.register

    case GET(p"/user") => studentController.getCurrentUser

    case PUT(p"/user") => studentController.update
  }
}
