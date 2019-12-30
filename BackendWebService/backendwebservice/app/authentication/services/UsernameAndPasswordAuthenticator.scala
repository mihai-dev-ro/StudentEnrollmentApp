package authentication.services

import authentication.api.{Authenticator, SecurityUserProvider, TokenGenerator}
import authentication.exceptions.InvalidPasswordException
import authentication.models._
import org.mindrot.jbcrypt.BCrypt
import play.api.mvc.Request
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

private[authentication] class UsernameAndPasswordAuthenticator(
  securityUserProvider: SecurityUserProvider,
  tokenGenerator: TokenGenerator[SecurityUserId, JwtToken])(
  implicit val executionContext: ExecutionContext)
  extends Authenticator[CredentialsWrapper] {

  override def authenticate(request: Request[CredentialsWrapper]): DBIO[String] = {
    require(request != null)

    val CredentialsWrapper(email, password) = request.body

    securityUserProvider.findByEmail(email)
      .map(securityUser => {
        if (authenticated(password, securityUser))
          tokenGenerator.generate(securityUser.id).token
        else
          throw new InvalidPasswordException(email.value)
      })
  }

  def authenticated(plainTextPassword: PlainTextPassword, securityUser: SecurityUser): Boolean = {
    BCrypt.checkpw(plainTextPassword.value, securityUser.password.value)
  }
}
