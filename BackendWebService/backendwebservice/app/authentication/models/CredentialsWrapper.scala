package authentication.models

import play.api.libs.json.{Format, Json}

case class CredentialsWrapper(user: EmailAndPasswordCredentials)

object CredentialsWrapper {
  implicit val formatCredentialsWrapper: Format[CredentialsWrapper] =
    Json.format[CredentialsWrapper]
}
