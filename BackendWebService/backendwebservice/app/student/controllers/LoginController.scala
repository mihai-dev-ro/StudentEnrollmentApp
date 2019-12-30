package student.controllers

import authentication.api.Authenticator
import authentication.exceptions.{InvalidPasswordException, NotFoundSecurityUserException}
import authentication.models.CredentialsWrapper
import common.controllers.AbstractBaseController
import common.services.DbActionRunner
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, ControllerComponents}
import student.models.StudentDetailsWithToken
import student.services.StudentQueryService

class LoginController (
  authenticator: Authenticator[CredentialsWrapper],
  controllerComponents: ControllerComponents,
  studentQueryService: StudentQueryService,
  dbActionRunner: DbActionRunner
  )
  extends AbstractBaseController(controllerComponents) {

  def login: Action[CredentialsWrapper] = Action.async[CredentialsWrapper](validateJson) {
    request =>
      val email = request.body.email
      val loginAction = authenticator.authenticate(request)
        .zip(studentQueryService.getStudent(email))
        .map(tokenAndStudent => StudentDetailsWithToken(tokenAndStudent._2, tokenAndStudent._1))
        .map(Json.toJson(_))
        .map(Ok(_))

      dbActionRunner.runTransactionally(loginAction)
        .recover({
          case _: InvalidPasswordException | _: NotFoundSecurityUserException =>
            val violation = JsObject(Map("email sau pwd" -> Json.toJson(Seq("is invalid"))))
            val response = JsObject(Map("errors" -> violation))
            UnprocessableEntity(response)
        })
  }
}