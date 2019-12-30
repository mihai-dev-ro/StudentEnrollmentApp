package authentication.models

import common.models.Email

case class SecurityUserToUpdate(email: Option[Email], plainTextPassword: Option[PlainTextPassword])
