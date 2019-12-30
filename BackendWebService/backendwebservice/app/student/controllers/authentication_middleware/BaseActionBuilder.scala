package student.controllers.authentication_middleware

import authentication.exceptions.GenericException
import authentication.jwt.services.JwtAuthenticator
import play.api.mvc.RequestHeader
import slick.dbio.DBIO
import student.models.Student
import student.services.StudentQueryService

import scala.concurrent.ExecutionContext

class BaseActionBuilder(
  jwtAuthenticator: JwtAuthenticator,
  studentQueryService: StudentQueryService)(
  implicit ec: ExecutionContext) {

  def authenticate(requestHeader: RequestHeader): DBIO[(Student, String)] = {
    jwtAuthenticator
      .authenticate(requestHeader)
      .fold(
        authenticationExceptionCode => DBIO.failed(
          new GenericException(authenticationExceptionCode)),
        securityIdWithToken =>
          studentQueryService
            .getStudentBySecurityUserId(securityIdWithToken._1)
            .map(student => (student, securityIdWithToken._2))
      )
  }
}
