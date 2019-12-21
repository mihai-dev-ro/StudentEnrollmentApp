package student.controllers

import authentication.api._
import authentication.models.{JwtToken, SecurityUserId}
import common.controllers.AbstractBaseController
import common.services.DbActionRunner
import play.api.libs.json._
import play.api.mvc._
import student.models._
import student.services.{StudentRegistrationService, StudentService}

class UserController(authenticatedAction: AuthenticatedActionBuilder,
                     actionRunner: DbActionRunner,
                     studentRegistrationService: StudentRegistrationService,
                     studentService: StudentService,
                     jwtAuthenticator: TokenGenerator[IdProfile, JwtToken],
                     components: ControllerComponents)
  extends AbstractBaseController(components) {

  def update: Action[UpdateUserWrapper] = authenticatedAction.async(validateJson[UpdateUserWrapper]) { request =>
    val userId = request.user.userId
    actionRunner.runTransactionally(userService.update(userId, request.body.user))
      .map(userDetails => UserDetailsWithToken(userDetails, request.user.token))
      .map(UserDetailsWithTokenWrapper(_))
      .map(Json.toJson(_))
      .map(Ok(_))
      .recover(handleFailedValidation)
  }

  def getCurrentUser: Action[AnyContent] = authenticatedAction.async { request =>
    val userId = request.user.userId
    actionRunner.runTransactionally(userService.getUserDetails(userId))
      .map(userDetails => UserDetailsWithToken(userDetails, request.user.token))
      .map(UserDetailsWithTokenWrapper(_))
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def register: Action[UserRegistrationWrapper] = Action.async(validateJson[UserRegistrationWrapper]) { request =>
    actionRunner.runTransactionally(userRegistrationService.register(request.body.user))
      .map(user => {
        val jwtToken: JwtToken = generateToken(user.securityUserId)
        UserDetailsWithToken(user.email, user.username, user.createdAt, user.updatedAt, user.bio, user.image,
          jwtToken.token)
      })
      .map(UserDetailsWithTokenWrapper(_))
      .map(Json.toJson(_))
      .map(Ok(_))
      .recover(handleFailedValidation)
  }

  private def generateToken(securityUserId: SecurityUserId) = {
    val profile = IdProfile(securityUserId)
    jwtAuthenticator.generate(profile)
  }

}