package authentication.api

import authentication.models.{JwtToken, SecurityUserId}

trait TokenGenerator[A <: SecurityUserId, B <: JwtToken] {
  def generate(securityUserId: A): B
}
