package authentication.models

case class JwtToken(token: String)

object JwtToken {
  def apply(token: String) = new JwtToken(token)
  implicit def tokenToString(jwtToken: JwtToken): String = jwtToken.token
}
