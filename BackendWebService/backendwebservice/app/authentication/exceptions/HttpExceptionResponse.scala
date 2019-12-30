package authentication.exceptions

import play.api.libs.json.{Format, Json}

case class HttpExceptionResponse(exception: AuthenticationExceptionCode) {
  val message = exception.message
}

object HttpExceptionResponse {
  implicit val httpExceptionFormat:Format[HttpExceptionResponse] = Json.format[HttpExceptionResponse]
}
