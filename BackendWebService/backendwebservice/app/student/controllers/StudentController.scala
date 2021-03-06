package student.controllers

import authentication.api._
import authentication.models.{JwtToken, SecurityUserId}
import common.controllers.AbstractBaseController
import common.services.DbActionRunner
import play.api.libs.json._
import play.api.mvc._
import student.controllers.authentication_middleware.AuthenticatedActionBuilder
import student.models._
import student.services.{StudentQueryService, StudentRegistrationService, StudentUpdateService}

class StudentController (authenticatedAction: AuthenticatedActionBuilder,
                     actionRunner: DbActionRunner,
                     studentRegistrationService: StudentRegistrationService,
                     studentUpdateService: StudentUpdateService,
                     studentQueryService: StudentQueryService,
                     jwtAuthenticator: TokenGenerator[SecurityUserId, JwtToken],
                     components: ControllerComponents)
  extends AbstractBaseController(components) {

  def register: Action[StudentRegistrationWrapper] = Action.async(
    validateJson[StudentRegistrationWrapper]) { request =>

    actionRunner.runTransactionally(studentRegistrationService.register(request.body.student))
      .map(student => {
        val jwtToken: JwtToken = generateToken(student.securityUserId)
        StudentDetailsWithToken(student, jwtToken.token)
      })
      .map(StudentDetailsWithTokenWrapper(_))
      .map(Json.toJson(_))
      .map(Ok(_))
      .recover(handleFailedValidation)
  }

  def update: Action[StudentForUpdatingWrapper] = authenticatedAction.async(
    validateJson[StudentForUpdatingWrapper]) { request =>

    val studentId = request.user.studentId
    actionRunner.runTransactionally(studentUpdateService.update(studentId, request.body.student))
      .map(studentDetails => StudentDetailsWithToken(studentDetails, request.user.token))
      .map(StudentDetailsWithTokenWrapper(_))
      .map(Json.toJson(_))
      .map(Ok(_))
      .recover(handleFailedValidation)
  }

  def getCurrentUser: Action[AnyContent] = authenticatedAction.async { request =>
    val studentId = request.user.studentId
    actionRunner.runTransactionally(studentQueryService.getStudent(studentId))
      .map(studentDetails => StudentDetailsWithToken(studentDetails, request.user.token))
      .map(StudentDetailsWithTokenWrapper(_))
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  private def generateToken(securityUserId: SecurityUserId) = {
    jwtAuthenticator.generate(securityUserId)
  }

}