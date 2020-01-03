package authentication.models

import common.models.Email
import play.api.libs.json.{Format, Json}

case class EmailAndPasswordCredentials(email: Email, password: PlainTextPassword)

object EmailAndPasswordCredentials {
  implicit val formatEmailAndPasswordCredentials: Format[EmailAndPasswordCredentials] =
    Json.format[EmailAndPasswordCredentials]
}
