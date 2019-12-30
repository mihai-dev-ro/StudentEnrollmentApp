package authentication.jwt

import com.softwaremill.macwire._
import authentication.api.SecurityUserProvider
import authentication.jwt.services.{JwtAuthenticator, JwtTokenGenerator, SecretProvider}
import common.CommonComponents
import common.config.WithExecutionContextComponents
import play.api.Configuration
import play.api.mvc.PlayBodyParsers

trait JwtAuthComponents extends WithExecutionContextComponents with CommonComponents  {

  def configuration: Configuration

  def playBodyParsers: PlayBodyParsers

  def securityUserProvider: SecurityUserProvider

  lazy val secretProvider: SecretProvider = wire[SecretProvider]
  lazy val jwtTokenGenerator: JwtTokenGenerator = wire[JwtTokenGenerator]
  lazy val jwtAuthenticator: JwtAuthenticator = wire[JwtAuthenticator]
}
