package authentication.jwt.services

import java.time.{Duration, Instant}
import java.util.Date

import authentication.api.TokenGenerator
import authentication.models.{JwtToken, SecurityUserId}
import common.repositories.DateTimeProvider
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}

class JwtTokenGenerator(dateTimeProvider: DateTimeProvider,  secretProvider: SecretProvider)
  extends TokenGenerator[SecurityUserId, JwtToken] {

    private val tokenDuration = Duration.ofHours(1)

    override def generate(securityUserId: SecurityUserId): JwtToken = {
      var signedToken = Jwts.builder().setExpiration(Date.from(expiredAt))
        .claim(JwtTokenGenerator.securityUserIdClaimName, securityUserId.value.toString)
        .signWith(SignatureAlgorithm.HS256, secretProvider.get)
        .compact()

      JwtToken(signedToken)
    }

    private def expiredAt: Instant = {
      val now = dateTimeProvider.now
      now.plus(tokenDuration)
    }
  }

private[authentication] object JwtTokenGenerator {
  val securityUserIdClaimName: String = "security_user_id"
}
