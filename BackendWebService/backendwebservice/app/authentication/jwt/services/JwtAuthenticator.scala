package authentication.jwt.services

import authentication.exceptions._
import authentication.models.{JwtToken, SecurityUserId}
import common.repositories.DateTimeProvider
import io.jsonwebtoken.{Claims, Jws, JwtException, Jwts}
import play.api.mvc.RequestHeader
import play.mvc.Http

class JwtAuthenticator(secretProvider: SecretProvider, dateTimeProvider: DateTimeProvider) {
  def authenticate(requestHeader: RequestHeader):
    Either[AuthenticationExceptionCode, (SecurityUserId, JwtToken)] = {

    requestHeader.headers.get(Http.HeaderNames.AUTHORIZATION)
      .map(parse)
      .toRight(MissingOrInvalidCodeException)
      .map(JwtToken(_))
      .flatMap(validate)
  }

  def parse(authorizationHeader: String): String = {
    authorizationHeader.stripPrefix("Token ")
  }

  def validate(jwtToken: JwtToken):
    Either[AuthenticationExceptionCode, (SecurityUserId, JwtToken)] = {

    try {
      val jwt = Jwts.parser()
        .setSigningKey(secretProvider.get)
        .parseClaimsJws(jwtToken)

      if (expirationIsMissing(jwt)) Left(MissingOrInvalidCodeException)
      else if (notExpired(jwt)) Right((getSecurityUserId(jwt), jwtToken))
      else Left(ExpiredCodeException)
    } catch {
      case e: JwtException =>
        Left(new InvalidCodeException(e.getMessage()))
    }
  }

  def expirationIsMissing(jwt: Jws[Claims]): Boolean = {
    jwt.getBody.getExpiration == null
  }

  def notExpired(jwt: Jws[Claims]): Boolean = {
    val expirationDate = jwt.getBody.getExpiration.toInstant
    dateTimeProvider.now.isBefore(expirationDate)
  }

  def getSecurityUserId(jwt: Jws[Claims]): SecurityUserId = {
    val securityUserId = java.lang.Long.parseLong(
      jwt.getBody.get(JwtTokenGenerator.securityUserIdClaimName, classOf[String]))
    SecurityUserId(securityUserId)
  }
}
