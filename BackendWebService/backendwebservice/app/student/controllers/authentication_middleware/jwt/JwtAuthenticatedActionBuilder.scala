package student.controllers.authentication_middleware.jwt

import authentication.exceptions.{AuthenticationExceptionCode, GenericException, HttpExceptionResponse}
import authentication.jwt.services.JwtAuthenticator
import common.services.DbActionRunner
import play.api.libs.json.Json
import play.api.mvc
import play.api.mvc._
import play.api.mvc.Results._
import student.controllers.authentication_middleware._
import student.models.AuthenticatedStudent
import student.services.StudentQueryService

import scala.concurrent.{ExecutionContext, Future}

class JwtAuthenticatedActionBuilder(
  parsers: PlayBodyParsers,
  jwtAuthenticator: JwtAuthenticator,
  studentQueryService: StudentQueryService,
  dbActionRunner: DbActionRunner)(
  implicit val ec: ExecutionContext)
  extends BaseActionBuilder(jwtAuthenticator, studentQueryService)
  with AuthenticatedActionBuilder {

  override def parser: BodyParser[AnyContent] = new mvc.BodyParsers.Default(parsers)

  override def invokeBlock[A](request: Request[A],
    block: AuthenticatedStudentRequest[A] => Future[Result]): Future[Result] = {

    dbActionRunner.runTransactionally(authenticate(request))
      .map(studentWithToken => new AuthenticatedStudent(studentWithToken._1.id,
        studentWithToken._1.securityUserId, studentWithToken._2))
      .flatMap(authenticateStudent => {
        val authRequest = new AuthenticatedStudentRequest(authenticateStudent, request)
        block(authRequest)
      })
      .recover({
        case e: GenericException => onUnauthorized(e.exception, request)
      })
  }

  override protected def executionContext: ExecutionContext = ec

  def onUnauthorized(code: AuthenticationExceptionCode, value: RequestHeader) = {
    val response = HttpExceptionResponse(code)
    Unauthorized(Json.toJson(response))
  }
}