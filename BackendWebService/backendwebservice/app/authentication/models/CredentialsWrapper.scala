package authentication.models

import common.models.Email
import play.api.libs.json.{Format, Json}

case class CredentialsWrapper(email: Email, password: PlainTextPassword)

object CredentialsWrapper {
  implicit val formatCredentialsWrapper: Format[CredentialsWrapper] =
    Json.format[CredentialsWrapper]
}
