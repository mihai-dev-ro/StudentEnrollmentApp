package student

import akka.stream.Materializer
import com.softwaremill.macwire._
import play.modules.benji.BenjiComponents
import authentication.AuthenticationComponents
import com.zengularity.benji.ObjectStorage
import common.config.WithControllerComponents
import common.services.FileCloudUploaderService
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.mvc.PlayBodyParsers
import play.api.routing.Router
import play.api.routing.sird._
import student.controllers.authentication_middleware.AuthenticatedActionBuilder
import student.controllers.authentication_middleware.jwt.JwtAuthenticatedActionBuilder
import student.controllers._
import student.models.StudentApplicationId
import student.repositories._
import student.services._
import university.repository.UniversityRepo
import university.services.{UniversityQueryExternalProvider, UniversityQueryProvider, UniversityUpdateService}

trait StudentComponents
  extends AuthenticationComponents {

  def playBodyParsers: PlayBodyParsers

  def universityQueryProvider: UniversityQueryProvider
  def universityQueryExternalProvider: UniversityQueryExternalProvider
  def universityUpdateService: UniversityUpdateService

  //repo
  lazy val studentRepo: StudentRepo = wire[StudentRepo]
  lazy val studentApplicationRepo: StudentApplicationRepo = wire[StudentApplicationRepo]

  // utilities
  lazy val passwordValidator: PasswordValidator = wire[PasswordValidator]
  lazy val emailValidator: EmailValidator = wire[EmailValidator]
  lazy val studentRegistrationValidator: StudentRegistrationValidator =
    wire[StudentRegistrationValidator]
  lazy val studentUpdateValidator: StudentUpdateValidator = wire[StudentUpdateValidator]
  lazy val studentApplicationValidator: StudentApplicationValidator =
    wire[StudentApplicationValidator]

  // services
  lazy val studentRegistrationService: StudentRegistrationService = wire[StudentRegistrationService]
  lazy val studentQueryService: StudentQueryService = wire[StudentQueryService]
  lazy val studentUpdateService: StudentUpdateService = wire[StudentUpdateService]
  lazy val studentApplicationService: StudentApplicationService =
    wire[StudentApplicationService]

  // custom actions
  lazy val jwtAuthenticatedActionBuilder: AuthenticatedActionBuilder =
    wire[JwtAuthenticatedActionBuilder]

  // controllers
  lazy val studentApplicationController: StudentApplicationController =
    wire[StudentApplicationController]
  lazy val loginController: LoginController = wire[LoginController]
  lazy val studentController: StudentController = wire[StudentController]

  // routes
  val studentRoutes: Router.Routes = {
    case POST(p"/users/login") => loginController.login

    case POST(p"/users") => studentController.register

    case GET(p"/user") => studentController.getCurrentUser

    case PUT(p"/user") => studentController.update

    case POST(p"/user/file") => studentApplicationController.uploadFile

    case GET(p"/user/file" ? q_o"name=$fileName") =>
      studentApplicationController.getFile(fileName)

    case GET(p"/user/file/metadata" ? q_o"name=$fileName") =>
      studentApplicationController.getFileMetadata(fileName)

    case POST(p"/user/applications") => studentApplicationController.startApplication

    case GET(p"/user/applications") => studentApplicationController.showAllApplications

    case GET(p"/user/application/$id") =>
      studentApplicationController.showApplication(StudentApplicationId(id.toInt))

    case POST(p"/user/application/$id/file") =>
      studentApplicationController.uploadFileForApplication(
        StudentApplicationId(id.toInt))
  }
}
