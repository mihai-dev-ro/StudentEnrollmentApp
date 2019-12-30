package authentication.exceptions

import play.api.libs.json._
import julienrf.json.derived

sealed trait AuthenticationExceptionCode {
  def message: String = ""
}

object AuthenticationExceptionCode {
  implicit val jsonFormat: Format[AuthenticationExceptionCode] = derived.flat
    .oformat((__ \ "type").format[String])
}

case object MissingOrInvalidCodeException extends AuthenticationExceptionCode {
  override val message: String = "Token-ul Jwt transmis in header-ul Authorization lipseste" +
    "sau este invalid"
}

case object ExpiredCodeException extends AuthenticationExceptionCode

case class InvalidCodeException(override val message: String) extends AuthenticationExceptionCode
