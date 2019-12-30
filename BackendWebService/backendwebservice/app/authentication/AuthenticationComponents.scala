package authentication

import com.softwaremill.macwire._
import authentication.api.{Authenticator, SecurityUserCreator, SecurityUserProvider, SecurityUserUpdater}
import authentication.jwt.JwtAuthComponents
import authentication.models.CredentialsWrapper
import authentication.repositories.SecurityUserRepo
import authentication.services.{SecurityUserService, UsernameAndPasswordAuthenticator}
import common.CommonComponents
import common.config.{WithControllerComponents, WithExecutionContextComponents}

trait AuthenticationComponents
  extends CommonComponents
  with WithControllerComponents
  with WithExecutionContextComponents
  with JwtAuthComponents {

  // repo
  lazy val securityUserRepo:SecurityUserRepo = wire[SecurityUserRepo]

  // utilities
  lazy val usernameAndPasswordAuthenticator:Authenticator[CredentialsWrapper] =
    wire[UsernameAndPasswordAuthenticator]

  // services
  lazy val securityUserCreator: SecurityUserCreator = wire[SecurityUserService]
  lazy val securityUserProvider: SecurityUserProvider = wire[SecurityUserService]
  lazy val securityUserUpdater: SecurityUserUpdater = wire[SecurityUserService]
}
